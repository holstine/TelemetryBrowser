package com.holstine.avc.telemetry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.holstine.avc.telemetry.StructuredDataListener.DataListener;

public class VariableListListenerView extends ListView implements DataListener {
	 private static final int REQUEST_CODE = 10;

	List<HashMap<String, String>>	fillMaps	= new ArrayList<HashMap<String, String>>();

	ArrayList<String>				tags		= new ArrayList<String>();

	private SimpleAdapter	adapter;

	public VariableListListenerView(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addVariableListUI();
		this.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
				Log.i("List View Clicked", "**********");
				HashMap<String, String> hm = fillMaps.get(position);
				String key = hm.get("Key");
				Toast.makeText(context, "List View Clicked:" + key, Toast.LENGTH_LONG).show();
				
				Intent i = new Intent(context, VariableEditActivity.class);
			    i.putExtra("yourkey", key);
			    context.startActivity(i);
			}
		});
	}

	public VariableListListenerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void addVariableListUI() {
		String[] from = new String[] { "Key", "Value" };
		int[] to = new int[] { R.id.item1, R.id.item2 };

		// prepare the list of all records

		adapter = new SimpleAdapter(this.getContext(), fillMaps, R.layout.grid_layout, from, to);
		setAdapter(adapter);

	}

	@Override
	public void receiveMessage(String tag, String string) {
		 
	//	Log.d("tag:", tag+ "  :  "+ string +"  : " );
		int index = tags.indexOf(tag);
	
		if (index >= 0) {
			HashMap<String, String> map = fillMaps.get(index);
			map.put("Value", string);
		//	Log.d("VariableL:", tag+ "  :  "+ string +"  : "+ index);
		} else {
			tags.add(tag);
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("Key", tag);
			hm.put("Value", string);
			fillMaps.add(hm);
			
			

		}
		adapter.notifyDataSetChanged();
	}

}
