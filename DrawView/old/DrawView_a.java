package com.naens.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawView extends ImageView {

	private Bitmap bmp;
	private Canvas canvas;
	private Path path;
	private Paint paint;
	private int strokeWidth = 4;
	private static double NORMAL_LENGTH = 16;
	private static double MIN_LENGTH = 2;
	private static double MIN_LENGTH_FACTOR = 1.25;
	private int color = Color.BLACK;

	public DrawView(Context context) {
		super(context);
		init();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void clear() {
		bmp = Bitmap.createBitmap(DrawView.this.getWidth(), DrawView.this.getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bmp);
		DrawView.this.setImageBitmap(bmp);
	}

	private void init() {
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(strokeWidth);
		paint.setAntiAlias(true);
		setOnTouchListener(new OnTouchListener() {
			private float x, y;
			private boolean hasline = false;
			private List <Float> lengthList = new LinkedList<Float>();

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (bmp == null) {
					bmp = Bitmap.createBitmap(DrawView.this.getWidth(), DrawView.this.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bmp);
					path = new Path();
				}

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						x = event.getX();
						y = event.getY();
						path.reset();
						path.moveTo(x, y);
//						Log.i ("TAG", "down:" + x + "," + y);
						hasline = false;
						return true;
					}
					case MotionEvent.ACTION_MOVE: {
						float newX = event.getX();
						float newY = event.getY();
						float length = (float) Math.abs(Math.sqrt(Math.pow(newX - x, 2) + Math.pow(newY - y, 2)));
						if (lengthList.size() == 100) {
							float sumLength = 0;
							for (float f : lengthList) {
								sumLength += f;
							}
							Log.i ("TAG", "Mlength:" + sumLength / 100.0);
							lengthList.clear();
						} else {
							lengthList.add(length);
						}
						
//						Log.i ("TAG", "move:" + newX + "," + newY);
//						Log.i ("TAG", "length:" + length);
						double s = Math.pow(MIN_LENGTH_FACTOR, 1 / (MIN_LENGTH - NORMAL_LENGTH));
						double u = Math.pow(s, - NORMAL_LENGTH);
						double w = (u * Math.pow (s, length));
						float tempWidth = (float) (strokeWidth * w);
						hasline = true;
						paint.setStrokeWidth(tempWidth);
						path.lineTo(newX, newY);

						canvas.drawPath(path, paint);
						DrawView.this.setImageBitmap(bmp);
						x = newX;
						y = newY;
						path.reset();
						path.moveTo(x, y);
						return true;
					}
					case MotionEvent.ACTION_UP: {
						float newX = event.getX();
						float newY = event.getY();
//						Log.i ("TAG", "Up:" + newX + "," + newY);
						
						if (x == newX && y == newY && !hasline) {
							paint.setStrokeWidth((float) (strokeWidth * 1.4));
							path.lineTo(newX + (float)0.01, newY + (float)0.01);

							canvas.drawPath(path, paint);
							DrawView.this.setImageBitmap(bmp);
							paint.setStrokeWidth(strokeWidth);
						}
						return true;
					}
				}
				return true;
			}
		});
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
