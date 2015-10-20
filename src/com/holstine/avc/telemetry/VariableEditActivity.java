package com.holstine.avc.telemetry;

import com.holstine.avc.telemetry.StructuredDataListener.DataListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VariableEditActivity extends Activity implements DataListener, OnSeekBarChangeListener {

		String key ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_variable_edit);
		 Bundle extras = getIntent().getExtras();
		    String inputString = extras.getString("yourkey");
		    key = inputString;
		    EditText title =(EditText)findViewById( R.id.variableTitle);
		    
		    AVCTelemetry.dataListener.addListener(inputString, this);
		    ((SeekBar) findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(this);
		    title.setText(inputString);
		if (savedInstanceState == null) {
	//		getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public void receiveMessage(String tag, String string) {
		  EditText value =(EditText)findViewById( R.id.variableValue);
		value.setText(string);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		View v = (View) seekBar;
		 String msg =key +":"+ progress+"\n";
		 Log.d("slider",msg); 
			AVCTelemetry.instance.sendMessage(msg); 
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
