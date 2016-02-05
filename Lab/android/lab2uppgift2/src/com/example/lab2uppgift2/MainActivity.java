package com.example.lab2uppgift2;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

public class MainActivity extends Activity implements Runnable {
	private UsbFragment usbFragment;
	private UsbManager mUsbManager;
	private UsbAccessory mUsbAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		usbFragment = new UsbFragment();
		showFragment(usbFragment);
		mUsbManager = (UsbManager) getSystemService(USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commit();
	}

	@Override
	protected void onResume() {
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				// Accessoar är inkopplad och appen har fått rättigheter att
				// koppla upp sig mot den
				openAccessory(accessory);// ?
			} else {
				// Accessoar är inkopplad, men appen saknar rättigheter
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			// Det finns ingen accessoar inkopplad
		}
		super.onResume();
	}

	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mUsbAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "DA119A1");
			thread.start();
		} else {
			// Failed to connect
		}
	}

	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mUsbAccessory = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeAccessory();
	}

	private static final String ACTION_USB_PERMISSION = "se.mah.da171a.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory acc = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(acc);
					} else {
						// Användaren gav inte tillstånd
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory acc = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (acc != null && acc.equals(mUsbAccessory)) {
					closeAccessory();
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	@Override
	public void run() {
		int len = 0;
		byte[] buffer = new byte[16384];
		while (len >= 0) {
			try {
				len = mInputStream.read(buffer);
				// Gör något med den data som du tar emot
				mHandler.obtainMessage(READ_MESSAGE, len, -1, buffer)
				.sendToTarget();
			} catch (IOException e) {
				break;
			}
		}

	}

	private static final int READ_MESSAGE = 10;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == READ_MESSAGE) {
			int len = msg.arg1;
			byte[] buf = (byte[]) msg.obj;
			if (len > 0)
				usbFragment.setProgress(buf[0]);
			}
		}
	};

	public void write(byte[] buf) {
		try {
			mOutputStream.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send (byte[] bs) {
		write(bs);
	}
	
}
