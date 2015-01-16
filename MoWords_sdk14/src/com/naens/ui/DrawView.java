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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DrawView extends ImageView {

	public enum PenMode {DRAW, ERASE}

	private Bitmap bmp;
	private Canvas canvas;
	private Path path;
	private Paint paint;
	private float strokeWidth = (float) 2.5;
	private float erasorWidth = (float) 18;
	private static double NORMAL_LENGTH = 12;
	private static double MIN_LENGTH = 2;
	private static double MIN_LENGTH_FACTOR = 1.15;
	private int color = Color.BLACK;
	private int distancePerDrawAction = 400;
	private int distancePerEraseAction = 800;
	private List <DrawingAction> actions = new LinkedList <DrawingAction>();
	private List <DrawingAction> futureActions = new LinkedList <DrawingAction>();
	private DrawingAction.Path currentPath;
	private PenMode penMode = PenMode.DRAW;
	private PenModeListener penModeListener;
	private Integer preferedWidth;
	private Integer preferedHeight;
	
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
		clearBitmap ();
		if (actions.size() == 0 || !(actions.get(actions.size() - 1) instanceof DrawingAction.Clean)) {
			DrawingAction clean = new DrawingAction.Clean();
			actions.add(clean);
		}
		futureActions.clear();
		setPenMode(PenMode.DRAW);
	}

	private void init() {
		setScaleType(ScaleType.CENTER);
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(strokeWidth);
		paint.setAntiAlias(true);
		setOnTouchListener(new OnTouchListener() {
			private PointF current = new PointF();
			private PointF pre1 = null;
			private PointF pre2 = null;
			private PointF pre3 = null;
			private Line line1 = null;
			private Line line2 = null;
			private Path tempPath = null;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (bmp == null) {
					bmp = Bitmap.createBitmap(DrawView.this.getWidth(), DrawView.this.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bmp);
					path = new Path();
				}

				try {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: {
							current = new PointF (event.getX(), event.getY());
							path.reset();
							path.moveTo(current.x, current.y);
//							Log.i ("TAG", String.format("down: %.0f,%.0f", current.x ,current.y));
//							drawDot(current);
							tempPath = new Path(path);
							pre3 = pre2 = pre1 = null;
							futureActions.clear();
							return true;
						}
						case MotionEvent.ACTION_MOVE: {
							float x = event.getX();
							float y = event.getY();
							float distance = pointDistance (new PointF(x, y), current);
							if (distance < 8) {
								 return true;
							}
							path.reset();
							//make points
							pre3 = pre2;
							pre2 = pre1;
							pre1 = current;
							current = new PointF (x, y);
//							drawDot(current);
//							Log.i ("TAG", String.format("move: %.0f,%.0f", current.x ,current.y));
	
							if (current.x == pre1.x && current.y == pre1.y) {
								pre3 = pre2 = pre1 = null;
								line1 = line2 = null;
//								Log.i("TAG", "current.equals(pre1)");
								return true;
							}
							if (line1 != null) {
								tempPath.reset();
								canvas.drawPath(tempPath, paint);
							}
							//current != null, pre1 != null
							if (pre2 == null) {
								currentPath = new DrawingAction.Path(penMode);
							} else {
								if (pre3 == null) {
								} else {
									//make l2 from l1
									line2 = line1;
								}
								//make l1 from (2)-1-(0)
								float distK = pointDistance(pre1, pre2) / pointDistance(current, pre1);
								float ppx = pre1.x + (pre1.x - current.x) * distK;
								float ppy = pre1.y + (pre1.y - current.y) * distK;
								line1 = new Line(new PointF((ppx + pre2.x) / 2, (ppy + pre2.y) / 2), pre1);
							}
	
							if (pre2 != null) {
								Line p2p1 = new Line (pre2, pre1);
								if (p2p1.contains(current)) {
//									Log.i("TAG", "p2p1.contains(current)");
	//								paint.setColor(Color.YELLOW);
									path.moveTo(pre2.x,  pre2.y);
									path.lineTo(pre1.x,  pre1.y);
									currentPath.addLine(pre2, pre1);
									Log.i("TAG", "line p1-p2 (158)");
									checkPathLength();
									canvas.drawPath(path, paint);
	//								setImageBitmap(bmp);
//									Log.i("TAG", String.format("line: %.0f,%.0f -> %.0f,%.0f [YELLOW]",
//											pre2.x, pre2.y, pre1.x, pre1.y));
									pre3 = pre2 = pre1 = null;
									DrawView.this.setImageBitmap(bmp);
									return true;
								}
	
								if (pre3 != null) {
									if(line1.isParallelTo(line2)) {
//										Log.i("TAG", String.format("equal a!!!!!!!!!!!!!????????????????????"));
	//									paint.setColor(Color.WHITE);
										path.moveTo(pre2.x,  pre2.y);
										path.quadTo(pre1.x,  pre1.y, current.x, current.y);
										currentPath.addQuad(pre2, current, pre1);
										checkPathLength();
										canvas.drawPath(path, paint);
//										Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f) [WHITE]",
//												pre2.x, pre2.y, current.x, current.y, pre1.x, pre1.y));
										pre3 = pre2 = pre1 = null;
										DrawView.this.setImageBitmap(bmp);
										return true;
									} else {
										PointF p = line2.getCrossPoint(line1);
//										Log.i("TAG", String.format("point[p] %.0f,%.0f", p.x, p.y));
										Line l3 = p2p1.getParallelLine(pre3);
										Line l0 = p2p1.getParallelLine(current);
										Line l1 = p2p1.getPerpendicularLine(pre1);
										PointF q0 = l1.getCrossPoint(l0);
										PointF q3 = l1.getCrossPoint(l3);
										float q3q0 = pointDistance(q3, q0);
										float p1q3 = pointDistance(pre1, q3);
										float p1q0 = pointDistance(pre1, q0); 
										if (q3q0 > p1q3 && q3q0 > p1q0) {
											//case 1 => two quads
	//										paint.setColor(Color.RED);
											PointF m = new PointF((pre1.x + pre2.x)/2, (pre1.y + pre2.y)/2);
//											drawDot(m);
											Line pm = new Line(m, p);
											PointF pm_mid2 = midPoint (pre2, m);
											PointF pm_mid1 = midPoint (pre1, m);
											Line ppm2 = pm.getPerpendicularLine(pm_mid2);
											Line ppm1 = pm.getPerpendicularLine(pm_mid1);
											PointF q2 = line2.getCrossPoint(ppm2);
											PointF q1 = line1.getCrossPoint(ppm1);
//											Log.i("TAG", String.format("point[q2] %.0f,%.0f", q2.x, q2.y));
//											Log.i("TAG", String.format("point[q1] %.0f,%.0f", q1.x, q1.y));
											PointF c2, c1 = null;
											Line lm;
											if (pointDistance(pm_mid1, q1) > 1.5 *pointDistance(pre1, m) ){
												lm = line1.getPerpendicularLine(m);
												c2 = lm.getCrossPoint(line2);
//												Log.i("TAG", "--------------> 1");
											} else if (pointDistance(pm_mid1,q2) > 1.5 * pointDistance(pre2, m) ){
												lm = line2.getPerpendicularLine(m);
												c2 = lm.getCrossPoint(line2);
//												Log.i("TAG", "--------------> 2");
											} else {
												Line q1m = new Line(q1, m);
												PointF q2_1 = q1m.getCrossPoint(line2);
//												Log.i("TAG", String.format("point[q2_1] %.0f,%.0f", q2_1.x, q2_1.y));
												c2 = midPoint(q2, q2_1);
												lm = new Line(c2, m);
											}
//											Log.i("TAG", String.format("point[c2] %.0f,%.0f", c2.x, c2.y));
											c1 = lm.getCrossPoint(line1);
//											Log.i("TAG", String.format("point[c1] %.0f,%.0f", c1.x, c1.y));
	
											path.moveTo(pre2.x, pre2.y);
											path.quadTo(c2.x, c2.y, m.x,  m.y);
											currentPath.addQuad(pre2, m, c2);
											checkPathLength();
//											Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f) [RED]",
//													pre2.x, pre2.y, m.x, m.y, c2.x, c2.y));
											canvas.drawPath(path, paint);
											setImageBitmap(bmp);
											path.reset();
											path.moveTo(m.x, m.y);
	//										paint.setColor(Color.LTGRAY);
											path.quadTo(c1.x, c1.y, pre1.x,  pre1.y);
											currentPath.addQuad(m, pre1, c1);
											checkPathLength();
//											Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f) [LTGRAY]",
//													m.x, m.y, pre1.x,  pre1.y, c1.x, c1.y));
											canvas.drawPath(path, paint);
										} else {
											float length = pointDistance (pre1,  current);
											double s = Math.pow(MIN_LENGTH_FACTOR, 1 / (MIN_LENGTH - NORMAL_LENGTH));
											double u = Math.pow(s, - NORMAL_LENGTH);
											double w = (u * Math.pow (s, length));
											@SuppressWarnings("unused")
											float tempWidth = (float) (strokeWidth * w);
	
											paint.setColor(Color.BLACK);
											//case 3  => one quad
		//									Log.i("TAG", String.format("cross line1(%s) x line 2(%s) = (%.0f,%.0f)",
		//											line1.toString(), line2.toString(), p.x, p.y));
											path.moveTo(pre2.x,  pre2.y);
	//										paint.setStrokeWidth(tempWidth);
											path.quadTo(p.x, p.y, pre1.x,  pre1.y);
											currentPath.addQuad(pre2, pre1, p);
											checkPathLength();
//											Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f)",
//													pre2.x, pre2.y, pre1.x, pre1.y, p.x, p.y));
											canvas.drawPath(path, paint);
										}
									}
								} else {
	//								paint.setColor(Color.BLUE);
									PointF midPoint = new PointF((pre2.x + pre1.x) / 2, (pre2.y + pre1.y) / 2);
									Line l01 = new Line(pre2, pre1);
									Line perp = l01.getPerpendicularLine(midPoint);
	
									path.moveTo(pre2.x,  pre2.y);
									if (perp.isParallelTo(line1)) {
										path.lineTo(pre1.x, pre1.y);
										currentPath.addLine(pre2, pre1);
										Log.i("TAG", "line p1-p2 (278)");
										checkPathLength();
//										Log.i("TAG", String.format("line: %.0f,%.0f -> %.0f,%.0f [BLUE]",
//												pre2.x, pre2.y, pre1.x, pre1.y));
									} else {
										PointF q = perp.getCrossPoint(line1);
										Line ppp = line1.getPerpendicularLine(pre2);
										float d = pointDistance(pre1, ppp.getCrossPoint(line1));
										if (pointDistance(pre1, q) / pointDistance(pre2, pre1) > 0.5) {
											q = new PointF(pre1.x + d * (q.x - pre1.x) / pointDistance(pre1, q),
													pre1.y + d * (q.y - pre1.y) / pointDistance(pre1, q));
										}
										path.quadTo(q.x, q.y, pre1.x, pre1.y);
										currentPath.addQuad(pre2, pre1, q);
										checkPathLength();
//										Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f) [BLUE]",
//											pre2.x, pre2.y, pre1.x, pre1.y, q.x, q.y));
									}
									canvas.drawPath(path, paint);
								}
							}
	//						path.reset();
	
							if (pre1 != null) {
							}
							if (line1 != null) {
							}
							DrawView.this.setImageBitmap(bmp);
	
							return true;
						}
						case MotionEvent.ACTION_UP: {
//							Log.i ("TAG", "up:" + current.x + "," + current.y);
							if (pre1 == null) {
								paint.setStrokeWidth(paint.getStrokeWidth() * (float)1.3);
								path.moveTo(current.x - (float) 0.005, current.y - (float) 0.005);
								path.lineTo(current.x + (float) 0.005, current.y + (float) 0.005);
								actions.add(new DrawingAction.Point(current, penMode));
	
								canvas.drawPath(path, paint);
								DrawView.this.setImageBitmap(bmp);
								paint.setStrokeWidth(paint.getStrokeWidth() / (float)1.3);
							} else if (pre2 == null) {
								path.moveTo(pre1.x,  pre1.y);
								path.lineTo(current.x, current.y);
								currentPath.addLine(pre1, current);
								actions.add(currentPath);
	
								canvas.drawPath(path, paint);
								DrawView.this.setImageBitmap(bmp);
							} else {
								if (pre3 == null) {
									line1 = new Line(pre1, pre2);
								}
	//							paint.setColor(Color.BLACK);
								path.moveTo(pre1.x,  pre1.y);
								PointF midPoint = new PointF((current.x + pre1.x) / 2, (current.y + pre1.y) / 2);
								Line l01 = new Line(current, pre1);
								Line perp = l01.getPerpendicularLine(midPoint);
								if (perp.isParallelTo(line1)) {
									path.lineTo(current.x, current.y);
									currentPath.addLine(pre1, current);
									actions.add(currentPath);
//									Log.i("TAG", String.format("line: %.0f,%.0f -> %.0f,%.0f [final]",
//											pre1.x, pre1.y, current.x, current.y));
								} else {
									PointF q = perp.getCrossPoint(line1);
//									Log.i("TAG", String.format("q: %.0f,%.0f", q.x, q.y));
									if (pointDistance(pre1, q) / pointDistance(current, pre1) > 0.75) {
										Line ppp = line1.getPerpendicularLine(current);
//										float d = pointDistance(pre1, ppp.getCrossPoint(line1));
//										q = new PointF(	pre1.x + d * (q.x - pre1.x) / pointDistance(pre1, q),
//														pre1.y + d * (q.y - pre1.y) / pointDistance(pre1, q));
//										Log.i("TAG", String.format("pre1-q / current-pre1 > 2 => new q: ", q.x, q.y));
										q=ppp.getCrossPoint(line1);
									}
									path.quadTo(q.x, q.y, current.x, current.y);
									currentPath.addQuad(pre1, current, q);
									actions.add(currentPath);
//									Log.i("TAG", String.format("quad: %.0f,%.0f -> %.0f,%.0f  (%.0f,%.0f) [final]",
//										pre1.x, pre1.y, current.x, current.y, q.x, q.y));
//									drawDot(q);
								}
								canvas.drawPath(path, paint);
								DrawView.this.setImageBitmap(bmp);

//								drawLine(line1, pre1);
							}
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
		});
	}

	protected void checkPathLength() {
		if (penMode.equals(PenMode.DRAW)&& currentPath.getLength() > distancePerDrawAction
				|| penMode.equals(PenMode.ERASE)&& currentPath.getLength() > distancePerEraseAction) {
			actions.add(currentPath);
			currentPath = new DrawingAction.Path(penMode);
		}		
	}

	private float pointDistance (PointF p1, PointF p2) {
		return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	private PointF midPoint(PointF p1, PointF p2) {
		return new PointF((p1.x + p2.x)/2, (p1.y + p2.y)/2);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	//debug functions
	private Paint debugPaint = new Paint();
	{
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setStrokeWidth(1);
	}

	@SuppressWarnings("unused")
	private void drawDot (final PointF p) {
//		Log.i("TAG", "draw dot: " + p.x + ", " + p.y);
		path.moveTo(p.x,  p.y);
		path.addCircle(p.x, p.y, 2, Path.Direction.CCW);
		canvas.drawPath(path, debugPaint);
//		setImageBitmap(bmp);
	}

	@SuppressWarnings("unused")
	private void drawLine (Line line, PointF p) {
		drawLine(line, p, Color.BLACK);
	}

	private void drawLine (Line line, PointF p, int color) {
		float length = 400;
		float x1, x2, y1, y2;
		if (line.isVertical()) {
			x1 = x2 = p.x;
			y1 = p.y - length / 2;
			y2 = p.y + length / 2;
		} else {
			x1 = (float) (p.x - Math.sqrt(Math.pow(length / 2, 2) / (1 + Math.pow(line.getA(), 2))));
			y1 = (float) (p.y - line.getA() * Math.sqrt(Math.pow(length / 2, 2) / (1 + Math.pow(line.getA(), 2))));
			x2 = (float) (p.x + Math.sqrt(Math.pow(length / 2, 2) / (1 + Math.pow(line.getA(), 2))));
			y2 = (float) (p.y + line.getA() * Math.sqrt(Math.pow(length / 2, 2) / (1 + Math.pow(line.getA(), 2))));
		}
		path.reset();
		path.moveTo(x1,  y1);
		path.lineTo(x2,  y2);
		debugPaint.setColor(color);
		canvas.drawPath(path, debugPaint);
		setImageBitmap(bmp);
//		Log.i("TAG", String.format("draw line a=%.0f (%.0f,%.0f): (%.0f,%.0f) -> (%.0f,%.0f)",
//				line.getA(), p.x, p.y, x1, y1, x2, y2));
		
	}

	public void back() {
		if (actions.size() > 0) {
			futureActions.add(actions.remove(actions.size() - 1));
			redrawActions ();
			setPenMode(PenMode.DRAW);
		}
	}

	private void drawAction (DrawingAction actionToDraw) {
		if (actionToDraw instanceof DrawingAction.Clean) {
			//clear
			clearBitmap ();
		} else {
			path.reset();
			internalChangePenMode (actionToDraw.getPenMode());
			actionToDraw.draw(path);
			canvas.drawPath(path, paint);
		}
	}

	public void forward() {
		if (futureActions.size() > 0) {
			DrawingAction actionToDraw = futureActions.remove(futureActions.size() - 1);
			actions.add(actionToDraw);
			drawAction(actionToDraw);
			setPenMode(PenMode.DRAW);
		}
	}

	private void clearBitmap () {
		if (bmp != null) {
			bmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(bmp);
			path = new Path();
			DrawView.this.setImageBitmap(bmp);
		}
	}

	private void redrawActions() {
		//clear
		clearBitmap ();
		if (actions.size() > 0) {
			//find last clean
			int i = actions.size() - 1;
			while (i >=0) {
				DrawingAction action = actions.get(i);
				if (action instanceof DrawingAction.Clean || i == 0) {
					break;
				}
				-- i;
			}
		//	redraw
			while (i <= actions.size() - 1) {
				DrawingAction action = actions.get(i);
				drawAction(action);
				++ i;
			}
		}
		setPenMode(PenMode.DRAW);
		
	}

	public void changePenMode() {
		setPenMode (penMode.equals(PenMode.DRAW) ? PenMode.ERASE : PenMode.DRAW);
	}

	private void internalChangePenMode (PenMode penMode) {
		if (penMode.equals(PenMode.DRAW)) {
			paint.setStrokeWidth(strokeWidth);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
		}
		if (penMode.equals(PenMode.ERASE)) {
			paint.setStrokeWidth(erasorWidth);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		}
	}

	public void setPenMode (PenMode penMode) {
		this.penMode = penMode;
		if (penModeListener != null) {
			penModeListener.penModeChanged (penMode);
		}
		internalChangePenMode (penMode);
	}

	public PenMode getPenMode() {
		return penMode;
	}

	public interface PenModeListener {
		void penModeChanged (PenMode penMode);
	}

	public PenModeListener getPenModeListener() {
		return penModeListener;
	}

	public void setPenModeListener(PenModeListener penModeListener) {
		this.penModeListener = penModeListener;
	}

	public void updateSize() {
		if (bmp != null) {
			int width = preferedWidth == null ? getWidth() : preferedWidth;
			int height = preferedHeight == null ? getHeight() : preferedHeight;
			Log.i("TAG", String.format("DrawView:updateSize: %dx%d -> %dx%d", bmp.getWidth(), bmp.getHeight(), width, height));
			int dx = (width - bmp.getWidth()) / 2;
			int dy = (height - bmp.getHeight()) / 2;
			bmp = Bitmap.createBitmap(width, height,  Bitmap.Config.ARGB_8888);
			for (DrawingAction action : actions) {
				action.shift (dx, dy);
			}
			for (DrawingAction action : futureActions) {
				action.shift (dx, dy);
			}
			redrawActions();
		}
	}

	public void setPreferedWidth(Integer preferedWidth) {
		this.preferedWidth = preferedWidth;
	}

	public void setPreferedHeight(Integer preferedHeight) {
		this.preferedHeight = preferedHeight;
	}

}
