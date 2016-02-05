package com.example.arduinoproject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import se.goransson.microbridge.android.AdbListener;
import se.goransson.microbridge.android.MicroBridge;

public class UsbListener implements AdbListener {	
	private MicroBridge usb;
	private ProgressBar mProgressBar;
	
	public UsbListener(ProgressBar mProgressBar) {
		this.mProgressBar = mProgressBar;
	}
	
	public void setMicroBridge(MicroBridge usb) {
		this.usb = usb;
	}

	@Override 
	public void adbConnected() { // TODO Auto-generated method stub 
	}
	
	@Override 
	public void adbDisconnected() { // TODO Auto-generated method stub 
	} 
		
	@Override 
	public void adbEvent(byte[] buffer) { // TODO Auto-generated method stub 	
		mProgressBar.setProgress(buffer[0]);
	}
}
