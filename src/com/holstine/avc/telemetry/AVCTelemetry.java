package com.holstine.avc.telemetry;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.Bluetooth.BluetoothController;
import com.holstine.avc.telemetry.StructuredDataListener.DataListener;

public class AVCTelemetry extends BluetoothController implements DataListener, OnSeekBarChangeListener {
	private BluetoothController				bluetooth;

	public  static StructuredDataListener	dataListener	= new StructuredDataListener();

	// String buffer for ingoing messages
	private StringBuffer					mInStringBuffer	= new StringBuffer("");			;

	int										test			= 0;

	private VariableListListenerView		list;

	private SeekBar	seekBar;
	public static AVCTelemetry instance;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		instance = this;
		setTabs();

		setupListeners();

	}

	private void setTabs() {
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Map");

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Text");
		spec2.setContent(R.id.tab2);

		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		spec3.setIndicator("Input");
		spec3.setContent(R.id.tab3);

		TabSpec spec4 = tabHost.newTabSpec("Tab 4");
		spec4.setIndicator("Compass");
		spec4.setContent(R.id.tab4);
		
		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);
		
	}

	private void setupListeners() {

		addListener(dataListener); // Bluetooth data listener to the structured
									// data
		PositionListener pos = new PositionListener();
		MagListener mag = new MagListener();
		
		pos.posX = (EditText) findViewById(R.id.pox);
		pos.posY = (EditText) findViewById(R.id.poy);
		pos.err = (EditText) findViewById(R.id.err);
		
		// DATA LISTENERS
		dataListener.addListener("rawGPS", pos);
		dataListener.addListener("currentPosition", pos);
		dataListener.addListener("currentWaypoint", pos);
		dataListener.addListener("heading", pos);
		dataListener.addListener("waypoints", pos);
		
		dataListener.addListener("heading", mag);
		dataListener.addListener("magno", mag);
		dataListener.addListener("compassHeading", mag);
		
		//GRAPH LISTENERS
		ListenerView graph = (ListenerView) findViewById(R.id.listenerView);
		ListenerView cgraph = (ListenerView) findViewById(R.id.listenerView2);
		cgraph .setScale(2f);
		graph.addDrawListener(pos);
		cgraph.addDrawListener(mag);
		list = (VariableListListenerView) findViewById(R.id.listview);
		


//			Read more: http://www.androidhub4you.com/2013/02/muftitouch-listview-multi-click.html#ixzz32GQHAwYa

		dataListener.addListener("*", list);
		dataListener.addListener("*", this);
	
		 ((SeekBar) findViewById(R.id.powerBar)).setOnSeekBarChangeListener(this);
		 ((SeekBar) findViewById(R.id.steeringBar)).setOnSeekBarChangeListener(this);
		 
		 ((SeekBar) findViewById(R.id.yOffsetCalibrationBar)).setOnSeekBarChangeListener(this);
		 ((SeekBar) findViewById(R.id.yScaleCalibrationBar)).setOnSeekBarChangeListener(this);
		 ((SeekBar) findViewById(R.id.xOffsetCalibrationBar)).setOnSeekBarChangeListener(this);
		 ((SeekBar) findViewById(R.id.xScaleCalibrationBar)).setOnSeekBarChangeListener(this);
		 
	}

	public void miscButton(View view){
		this.sendMessage(((Button) view).getHint().toString()+"\n");
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		String  msg = ((ToggleButton) view).getHint().toString();
		msg += on?"On":"Off";
		msg+="\n";
	 
			this.sendMessage(msg); 
	}
	
	public void receiveMessage(String tag, String string) {
		if("lcd1".equals(tag)){
			EditText lcd1 = (EditText)  findViewById(R.id.lcd1Text);
			lcd1.setText(string);
		}
		if("lcd2".equals(tag)){
			EditText lcd2 = (EditText)  findViewById(R.id.lcd2Text);
			lcd2.setText(string);
		}
		if("wayPointToRecord".equals(tag)){
			EditText wp = (EditText)  findViewById(R.id.waypointToRecord);
			wp.setText(string);
		}	
		if("fileNumber".equals(tag)){
			EditText wp = (EditText)  findViewById(R.id.playbackNumber);
			wp.setText(string);
		}	
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		View v = (View) seekBar;
		 String msg =v.getTag().toString() +":"+ progress+"\n";
		 Log.d("slider",msg); 
			this.sendMessage(msg); 
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}