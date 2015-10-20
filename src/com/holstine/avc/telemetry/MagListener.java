package com.holstine.avc.telemetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.EditText;

import com.holstine.avc.telemetry.ListenerView.DrawListener;
import com.holstine.avc.telemetry.StructuredDataListener.DataListener;

public class MagListener implements DataListener, DrawListener {
	private Paint		magPaint; 

	private Paint		headingPaint;
	private Paint		yawPaint;
  

	ArrayList<float[]>	magTrack	= new ArrayList<float[]>(100);
	int magCounter =0;
	 
 
	double				heading =0;
	double compassHeading =0;
	
	
	public MagListener() {
 

		headingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		headingPaint.setColor(Color.WHITE);
		headingPaint.setStrokeWidth((float) 1);
		headingPaint.setStyle(Paint.Style.STROKE);
		
		yawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		yawPaint.setColor(Color.GREEN);
		yawPaint.setStrokeWidth((float) 1);
		yawPaint.setStyle(Paint.Style.STROKE);

		magPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magPaint.setColor(Color.GREEN);
		magPaint.setStrokeWidth((float) 1);
		magPaint.setStyle(Paint.Style.FILL);

	}

	@Override
	public void receiveMessage(String tag, String string) {
	 
	
		if (tag.equals("magno")) { 
			String[] ps = string.split(",");
			float[] element = new float[3];
			element[0] = (Float.parseFloat(ps[0]));
			element[1] = (Float.parseFloat(ps[1]));
			element[2] = (Float.parseFloat(ps[2]));
			if (magTrack.size() > magCounter) {
				magTrack.set(magCounter++, element);
			} else {
				magTrack.add(magCounter++, element);
			}
			if (magCounter > 100)
				magCounter = 0; 
		}

		if (tag.equals("compassHeading")) {
			double head = Double.parseDouble(string);
			compassHeading = head;
		}
		
		if (tag.equals("heading")) {
			if(string.equals("nan")) return;
			double head = Double.parseDouble(string);
			heading = head;
		}
 
	}

	@Override
	public void draw(Canvas canvas) {
		// Log.d("pos", "painting element ");
		magPaint.setColor(Color.BLUE);
		canvas.drawCircle(0,0, 100f, magPaint);
		for (float[] element : magTrack) { 
			int currentIndex = magCounter == 0?0: magCounter-1 ;
			if(element == magTrack.get(currentIndex) ){
				magPaint.setColor(Color.RED);
			}else{
				magPaint.setColor(Color.GREEN);
			}
			canvas.drawCircle(element[1],-  element[0], 3f, magPaint);
		}
 
		float[] element = new float[3];
		element[0] =(50) * (float) Math.sin(Math.toRadians(compassHeading)); // northing
		element[1] = (50) * (float) Math.cos(Math.toRadians(compassHeading));// easting
		canvas.drawLine(0, 0,0 + element[0],0+ element[1], yawPaint);


		float[] helement = new float[3];
		helement[0] =(100) * (float) Math.sin(Math.toRadians(heading)); // northing
		helement[1] = (100) * (float) Math.cos(Math.toRadians(heading));// easting
		canvas.drawLine(0, 0,0 + helement[0],0+ helement[1], headingPaint);


		canvas.save();
	}

}
