package com.holstine.avc.telemetry;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class ListenerView extends PanZoomView {
	 private Paint gridPaint;
	 private Paint originPaint;
	ArrayList<DrawListener> drawListeners = new ArrayList<DrawListener>();
	public ListenerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		gridPaint.setColor(Color.YELLOW);
		gridPaint.setStrokeWidth((float) .1);
		gridPaint.setStyle(Paint.Style.STROKE);
		
		originPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		originPaint.setColor(Color.WHITE);
		originPaint.setStrokeWidth((float) .3);
		originPaint.setStyle(Paint.Style.STROKE);
	}
	public ListenerView (Context context, AttributeSet attrs) {
	    this(context, attrs, 0);
	}
	public void drawOnCanvas(Canvas canvas) {
	//	mSampleImage.draw(canvas);
		for(int i =-100; i < 100 ; i+= 5){
			canvas.drawLine(-100, i, 100, i, gridPaint);
			canvas.drawLine(i, -100, i, 100, gridPaint);
		}
		canvas.drawLine(-200, 0, 200,0, originPaint);
		canvas.drawLine(0, -200, 0, 200, originPaint);
		for(DrawListener dl: drawListeners){
			dl.draw(canvas);
		}
		this.invalidate();
	}
	public interface DrawListener{
		void draw(Canvas canvas);
		
	}
	
	public void addDrawListener(DrawListener dl){
		drawListeners.add(dl);
	}
}

