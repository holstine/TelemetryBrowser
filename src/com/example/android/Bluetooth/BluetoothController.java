/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.Bluetooth;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.holstine.avc.telemetry.DeviceListActivity;
import com.holstine.avc.telemetry.R;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothController extends Activity {
	// Debugging
	private static final String		TAG								= "BluetoothChat";

	private static final boolean	D								= true;

	// Message types sent from the BluetoothChatService Handler
	public static final int			MESSAGE_STATE_CHANGE			= 1;

	public static final int			MESSAGE_READ					= 2;

	public static final int			MESSAGE_WRITE					= 3;

	public static final int			MESSAGE_DEVICE_NAME				= 4;

	public static final int			MESSAGE_TOAST					= 5;

	// Key names received from the BluetoothChatService Handler
	public static final String		DEVICE_NAME						= "device_name";

	public static final String		TOAST							= "toast";

	// Intent request codes
	private static final int		REQUEST_CONNECT_DEVICE_SECURE	= 1;

	private static final int		REQUEST_CONNECT_DEVICE_INSECURE	= 2;

	private static final int		REQUEST_ENABLE_BT				= 3;

	// Layout Views
 

	// Name of the connected device
	private String					mConnectedDeviceName			= null;
 

	// String buffer for outgoing messages
	private StringBuffer			mOutStringBuffer				= new StringBuffer("");	;

	// Local Bluetooth adapter
	private BluetoothAdapter		mBluetoothAdapter				= null;

	// Member object for the chat services
	private BluetoothChatService	mChatService					= null;

	private ArrayList<BluetoothListener>	bluetoothListeners = new ArrayList<BluetoothController.BluetoothListener>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		setContentView(R.layout.main);

	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				mChatService = new BluetoothChatService(this, mHandler);
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}


	public void addListener(BluetoothListener bt){
		bluetoothListeners.add(bt);
	}
	/**
	 * Listen for the messages coming in from the bluetooth device.  add listeners 
	 * @param writeMessage
	 */
	public void readMessageFromBTServer(String readMessage) { 
		for(BluetoothListener bt: this.bluetoothListeners){
	//		Log.d("BlueTooth incoming", readMessage);
			bt.incomingMessage(readMessage);
		}
	}

	/**
	 * in case you want to listen to the OUTGOING messages from the android to the bluetooth device
	 * @param writeMessage
	 */
	public void messageWritenToBTServer(String writeMessage) {
		for(BluetoothListener bt: this.bluetoothListeners){
		//	Log.d("BlueTooth transmit", writeMessage);
			bt.outgoingMessage(writeMessage);
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	public void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

		}
	}

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler	mHandler	= new Handler() {
											@Override
											public void handleMessage(Message msg) {
												switch (msg.what) {
												case MESSAGE_STATE_CHANGE:
													if (D)
														Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
													switch (msg.arg1) {
													case BluetoothChatService.STATE_CONNECTED:
														setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
														// mConversationArrayAdapter.clear();
														break;
													case BluetoothChatService.STATE_CONNECTING:
														setStatus(R.string.title_connecting);
														break;
													case BluetoothChatService.STATE_LISTEN:
													case BluetoothChatService.STATE_NONE:
														setStatus(R.string.title_not_connected);
														break;
													}
													break;
												case MESSAGE_WRITE:
													byte[] writeBuf = (byte[]) msg.obj;
													// construct a string from
													// the buffer
													String writeMessage = new String(writeBuf);
													messageWritenToBTServer(writeMessage);
													break;
												case MESSAGE_READ:
													byte[] readBuf = (byte[]) msg.obj;
													// construct a string from
													// the valid bytes in the
													// buffer
													String readMessage = new String(readBuf, 0, msg.arg1);
													readMessageFromBTServer(readMessage);
													break;
												case MESSAGE_DEVICE_NAME:
													// save the connected
													// device's name
													mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
													Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
													break;
												case MESSAGE_TOAST:
													Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
													break;
												}
											}
										};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// setupDefaultChatUI();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}
	
	/**
	 * use this for listening to bluetooth messaging
	 * @author Michael
	 *
	 */
	public interface BluetoothListener{
		void incomingMessage(String message);
		void outgoingMessage(String message);
	}
}
