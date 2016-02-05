package com.example.lab2uppgift2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

public class UsbFragment extends Fragment implements OnClickListener{
	private ToggleButton tButt;
	private MainActivity activity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.usb, container, false);
		initializeComponents(view);
		return view;
	}

	private void initializeComponents(View view) {
		tButt = (ToggleButton) view.findViewById(R.id.toggleButton1);

		tButt.setOnClickListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = (MainActivity) getActivity();
	}

	boolean toggle = true;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.toggleButton1) {
			activity.send(toggle ? "H".getBytes() : "L".getBytes());
			toggle = !toggle;
		}
	}
}


