package com.naens.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class DrawView_Other extends ImageView {

	private Bitmap bmp;
	private Paint paint;
	private float strokeWidth = (float) 2.5;
	private static double NORMAL_LENGTH = 12;
	private static double MIN_LENGTH = 2;
	private static double MIN_LENGTH_FACTOR = 1.15;
	private int color = Color.BLACK;
	private Canvas canvas;
	private List <DrawSegment> currentSegments = new LinkedList<DrawView_Other.DrawSegment>();

	public DrawView_Other(Context context) {
		super(context);
		init();
	}

	public DrawView_Other(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawView_Other(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void clear() {
		bmp = Bitmap.createBitmap(DrawView_Other.this.getWidth(), DrawView_Other.this.getHeight(), Bitmap.Config.ARGB_8888);
		DrawView_Other.this.setImageBitmap(bmp);
		canvas = new Canvas(bmp);
	}

	public void back() {
		clear ();
		if (currentSegments.size() > 0) {
			currentSegments.remove(currentSegments.size() - 1);
			Path p = new Path();
			for (DrawSegment segment : currentSegments) {
				PointF p0 = segment.getP0();
				PointF p1 = segment.getP1();
				p.moveTo(p0.x,  p0.y);
				if (segment.isLine()) {
					p.lineTo(p1.x, p1.y);
				}
				if (segment.isQuad()) {
					PointF pc = segment.getPc();
					p.quadTo(pc.x, pc.y, p1.x, p1.y);
				}
			}
			canvas.drawPath(p, paint);
			DrawView_Other.this.setImageBitmap(bmp);
		}
	}

	private void init() {
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(strokeWidth);
		paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
		setOnTouchListener(new OnTouchListener() {
			private List<PointF> points = new LinkedList<PointF>();
			private Line line1 = null;
			private Line line2 = null;
			private Path path;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (bmp == null) {
					bmp = Bitmap.createBitmap(DrawView_Other.this.getWidth(), DrawView_Other.this.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bmp);
				}

				try {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: {
							path = new Path();
							addPoint(event.getX(), event.getY());
							Log.i ("TAG", String.format("down: %.0f,%.0f", points.get(0).x ,points.get(0).y));
							path.moveTo(points.get(0).x ,points.get(0).y);
							return true;
						}
						case MotionEvent.ACTION_MOVE: {
							addPoint(event.getX(), event.getY());
							Log.i ("TAG", String.format("move: %.0f,%.0f", points.get(0).x ,points.get(0).y));
							path.lineTo(points.get(0).x ,points.get(0).y);
							canvas.drawPath(path, paint);
							currentSegments.add(new DrawSegment(points.get(0), points.get(1)));
							DrawView_Other.this.setImageBitmap(bmp);
							return true;
						}
						case MotionEvent.ACTION_UP: {
							Log.i ("TAG", String.format("up"));
							points.clear();
							//currentSegments.clear();
							return true;
						}
					}
				} catch (Exception e) {
					String shortMsg = "MotionEvent, onTouch: Exception: " + e.getClass().getName();
					Toast.makeText(getContext(), shortMsg, Toast.LENGTH_LONG).show();
					Log.e("TAG", shortMsg, e);
				}
				return true;
			}

			private void addPoint (float x, float y) {
				points.add(0, new PointF (x, y));
				if (points.size() > 4) {
					points.remove(4);
				}
			}
		});
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	private class DrawSegment {
		PointF p0, p1, pc;
		boolean quad;
		boolean line;
		public DrawSegment(PointF p0, PointF p1) {
			super();
			this.p0 = p0;
			this.p1 = p1;
			line = true;
			quad = false;
		}
		public DrawSegment(PointF p0, PointF p1, PointF pc) {
			super();
			this.p0 = p0;
			this.p1 = p1;
			this.pc = pc;
			line = false;
			quad = true;
		}
		public PointF getP0() {
			return p0;
		}
		public PointF getP1() {
			return p1;
		}
		public PointF getPc() {
			return pc;
		}
		public boolean isQuad() {
			return quad;
		}
		public boolean isLine() {
			return line;
		}
		public void setLine() {
			line = true;
			quad = false;
		}
		public void setQuad (PointF pc) {
			this.pc = pc;
			line = false;
			quad = true;
		}
		
	}
}
