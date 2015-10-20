package com.holstine.avc.telemetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.EditText;

import com.holstine.avc.telemetry.ListenerView.DrawListener;
import com.holstine.avc.telemetry.StructuredDataListener.DataListener;

public class PositionListener implements DataListener, DrawListener {
	private Paint		posPaint;

	private Paint		cposPaint;

	private Paint		rawPaint;

	private Paint		headingPaint;

	private Paint		wayPaint;

	public EditText		posX;

	public EditText		posY;

	public EditText		err;

	ArrayList<float[]>	positionTrack	= new ArrayList<float[]>(100);

	ArrayList<float[]>	rawGPS			= new ArrayList<float[]>(100);

	int					posCounter		= 0;

	int					rawCounter		= 0;

	ArrayList<float[]>	allWaypoints	= new ArrayList<float[]>();

	float[]				currentPos		= new float[3];

	float[]				currentWaypoint	= new float[3];

	double				heading;

	public PositionListener() {
		posPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		posPaint.setColor(Color.BLUE);
		posPaint.setStrokeWidth((float) .1);
		posPaint.setStyle(Paint.Style.STROKE);

		cposPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cposPaint.setColor(Color.RED);
		cposPaint.setStrokeWidth((float) .3);
		cposPaint.setStyle(Paint.Style.STROKE);

		rawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rawPaint.setColor(Color.YELLOW);
		rawPaint.setStrokeWidth((float) .13);
		rawPaint.setStyle(Paint.Style.STROKE);

		headingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		headingPaint.setColor(Color.WHITE);
		headingPaint.setStrokeWidth((float) .1);
		headingPaint.setStyle(Paint.Style.STROKE);

		wayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayPaint.setColor(Color.GREEN);
		wayPaint.setStrokeWidth((float) .1);
		wayPaint.setStyle(Paint.Style.FILL);

	}

	@Override
	public void receiveMessage(String tag, String string) {
		if (tag.equals("rawGPS")) {
			String[] ps = string.split(",");
			float[] element = new float[3];

			element[0] = (Float.parseFloat(ps[0]));
			element[1] = (Float.parseFloat(ps[1]));
			element[2] = (Float.parseFloat(ps[2]));
			if (rawGPS.size() > rawCounter) {
				rawGPS.set(rawCounter++, element);
			} else {
				rawGPS.add(rawCounter++, element);
			}
			if (rawCounter > 100)
				rawCounter = 0;
		}

		if (tag.equals("currentPosition")) {
			String[] ps = string.split(",");
			posX.setText(ps[0]);
			posY.setText(ps[1]);
			err.setText(ps[2]);

			float[] element = new float[3];

			element[0] = (Float.parseFloat(ps[0]));
			element[1] = (Float.parseFloat(ps[1]));
			element[2] = (Float.parseFloat(ps[2]));
			currentPos = element;
			
			if (positionTrack.size() > posCounter) {
				positionTrack.set(posCounter++, element);
			} else {
				positionTrack.add(posCounter++, element);
			}
			if (posCounter > 100)
				posCounter = 0;
		}
		if (tag.equals("currentWaypoint")) {
			String[] ps = string.split(",");
			float[] element = new float[3];
			element[0] = (Float.parseFloat(ps[0]));
			element[1] = (Float.parseFloat(ps[1]));
			element[2] = (Float.parseFloat(ps[2]));

			currentWaypoint = element;
		}

		if (tag.equals("heading")) {
			if(string.equals("nan")) return;
			double head = Double.parseDouble(string);
		//	Log.d("heading", string);
			heading = head;
		}

		if (tag.equals("waypoints")) {
			allWaypoints.clear();
			String[] coords = string.split("!");

			for (String coord : coords) {
				Log.d("waypoints", "coords" + coord);
				String[] c = coord.split(",");
				if (c.length != 3) {
					Log.d("waypoints", "failed" + coord);
				} else {
					float[] f = new float[3];
					f[0] = Float.parseFloat(c[0]);
					f[1] = Float.parseFloat(c[1]);
					f[2] = Float.parseFloat(c[2]);
					allWaypoints.add(f);
					Log.d("addingWaypoints", "numWaypoints:" + allWaypoints.size());
				}

			}

		}

	}

	@Override
	public void draw(Canvas canvas) {
		// Log.d("pos", "painting element ");
		for (float[] element : positionTrack) {
			if (element[2] > 50)
				continue;
			canvas.drawCircle(element[0], element[1], element[2], posPaint);
		}

		float prevX = 0;
		float prevY = 0;
		int i =0;
		for (float[] element : allWaypoints) {
		
			canvas.drawCircle(element[0], element[1], 1, wayPaint);
			canvas.drawLine(prevX, prevY, element[0], element[1], wayPaint);
			rawPaint.setTextSize(2);
			rawPaint.setTextScaleX(-1);
			canvas.drawText(""+ i, element[0], element[1],rawPaint);
			prevX = element[0];
			prevY = element[1];
			i++;
		}

		canvas.drawCircle(currentPos[0], currentPos[1], currentPos[2], cposPaint);

		wayPaint.setColor(Color.WHITE);
		float diameter =currentWaypoint[2];
		if (diameter >10) diameter = .1f;
		canvas.drawCircle(currentWaypoint[0], currentWaypoint[1], diameter, wayPaint);
		wayPaint.setColor(Color.GREEN);
		float[] element = new float[3];
		element[0] =( currentPos[2] +1) * (float) Math.sin(Math.toRadians(heading)); // northing
		element[1] = (currentPos[2] +1) * (float) Math.cos(Math.toRadians(heading));// easting  
		canvas.drawLine(currentPos[0], currentPos[1], currentPos[0] + element[0], currentPos[1] + element[1], headingPaint);
		for (float[] element2 : rawGPS) {
			canvas.drawCircle(element2[0], element2[1], .15f, rawPaint);
		}
		canvas.save();
	}

}
