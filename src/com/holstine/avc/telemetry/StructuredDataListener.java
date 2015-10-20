package com.holstine.avc.telemetry;

import java.util.HashMap;
import java.util.HashSet;

import android.util.Log;

import com.example.android.Bluetooth.BluetoothController.BluetoothListener;

public class StructuredDataListener implements BluetoothListener {
	public static final char				TAG				= '\n';

	StringBuffer							buffer			= new StringBuffer(5000);

	HashMap<String, HashSet<DataListener>>	listeners		= new HashMap<String, HashSet<DataListener>>();

	HashSet<DataListener>					allListeners	= new HashSet<DataListener>();					// listens
																											// to
																											// all
																											// messages

	@Override
	public void incomingMessage(String readMessage) {

		for (int i = 0; i < readMessage.length(); i++) {
			char c = readMessage.charAt(i);

			if (TAG == c) {
				launchBuffer();
			} else {
				buffer.append(c);
			}
		}
		//
		// Log.d("CNCmsg:", readMessage);
		// // read incoming messages
		// if (!readMessage.contains(TAG)) {
		// buffer += (readMessage);
		// return;
		// }
		// if (readMessage.startsWith(TAG)) {
		// launchBuffer();
		// // buffer += (readMessage.replace(TAG, ""));
		// //
		// // return;
		// }
		//
		// String[] strings = readMessage.split(TAG);
		// int i = 0;
		// for (String string : strings) {
		// i++;
		// buffer += (string);
		// if (strings.length > 1 && i < strings.length) {
		// launchBuffer();
		// }
		// if(readMessage.endsWith(TAG)){
		// launchBuffer();
		// }

		// just start the buffer
		// with the last string
		// (if not trivial)
		return;
		// }

	}

	private void launchBuffer() {
		// I now have a complete sentence that can be parsed
		// Log.d("Data:", buffer.toString());
		String[] parsed = readLine(buffer);

		buffer.delete(0, buffer.length());
		if (parsed == null)
			return;
		if (parsed[0] == null)
			return;

		HashSet<DataListener> dl = listeners.get(parsed[0]);
		if (dl != null)
			for (DataListener lsnter : dl) {
				if (lsnter != null) {
					lsnter.receiveMessage(parsed[0], parsed[1]);
				}
			}
		if (allListeners != null) {
			for (DataListener dl2 : allListeners) {
				dl2.receiveMessage(parsed[0], parsed[1]);
			}
		}

	}

	private String[] readLine(StringBuffer string) {
		String[] output = new String[2];
		String[] tmp = string.toString().split(":");
		if ((tmp.length != 2))
		// ||
		// (!tmp[0].equals(tmp[1])) ||
		// (!tmp[2].equals(tmp[3])))
		{
			if (string.length() > 1)
				Log.d("failedTelemetry", string.toString());
			return null;
		}
		output[0] = tmp[0].trim();
		output[1] = tmp[1].trim();
		return output;
	}

	@Override
	public void outgoingMessage(String message) {
		// TODO Auto-generated method stub

	}

	public void addListener(String tag, DataListener dl) {
		if ("*".equals(tag)) {
			this.allListeners.add(dl);
		} else {
			HashSet<DataListener> dls = listeners.get(tag);
			if (dls == null) {
				dls = new HashSet<DataListener>();
				listeners.put(tag, dls);
			}

			dls.add(dl);
		}

	}

	public interface DataListener {
		void receiveMessage(String tag, String string);
	}
}
