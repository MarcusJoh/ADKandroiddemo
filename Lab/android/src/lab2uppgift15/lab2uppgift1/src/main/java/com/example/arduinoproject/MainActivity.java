package com.example.arduinoproject;

import se.goransson.microbridge.android.MicroBridge;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private MicroBridge usb;
	private UsbListener mUsbListener;
	private ProgressBar mProgressBar;
	private ToggleButton btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		btn = (ToggleButton) findViewById(R.id.toggleButton1);	
		mUsbListener = new UsbListener(mProgressBar);
		usb = new MicroBridge(mUsbListener);
		mUsbListener.setMicroBridge(usb);
		registerlisteners();
	}
	
	private void registerlisteners() {
		OnClickListener ToggleListener = new ToggleListener();
		btn.setOnClickListener(ToggleListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override 
	public void onResume() { 
		usb.connect(); 
		super.onResume(); 
	}
	
	@Override 
	public void onPause() { 
		usb.stop(); 
		super.onPause(); 
	}

	

	private class ToggleListener implements OnClickListener {
		@Override 
		public void onClick(View v) { 
			boolean on = ((ToggleButton) v).isChecked(); 
			if (on)
				usb.write("H".getBytes()); 
			else 
				usb.write("L".getBytes());
		}
	};
}
