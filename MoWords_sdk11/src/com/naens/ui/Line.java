package com.naens.ui;

import android.annotation.SuppressLint;
import android.graphics.PointF;

public class Line {

	private float a;

	private float b;

	private Float vertical = null;

	public Line (PointF p1, PointF p2) {
		if (p2.x == p1.x) {
			vertical = p2.x;
		} else {
			a = (p2.y - p1.y)/(p2.x - p1.x);
			b = (p2.x*p1.y - p1.x*p2.y)/(p2.x - p1.x);
		}
	}

	private Line (float a, float b) {
		this.a = a;
		this.b = b;
	}

	public Line(float x) {
		vertical = x;
	}

	public PointF getCrossPoint (Line line) {
		if (isVertical ()) {
			if (line.isVertical()) {
				throw new RuntimeException("find cross of two verical lines");
			}
			return new PointF (vertical, line.a*vertical + line.b);
		} else if (line.isVertical()) {
			return new PointF (line.vertical, a*line.vertical + b);
		}
		if (this.a == line.a) {
			throw new RuntimeException("lines parallel");
		}
		float x = (line.b - this.b)/(this.a - line.a);
		float y = (line.b * this.a - line.a * this.b)/(this.a - line.a);
		return new PointF (x,y);
	}

	public Line getPerpendicularLine (PointF p) {
		if (isVertical ()) {
			return new Line (0, p.y);
		}
		if (a == 0) {
			return new Line (p.x);
		}
		return new Line ((float)1.0/-a, p.y - ((float)1.0/-a * p.x));
	}

	public Line getParallelLine (PointF p) {
		if (isVertical ()) {
			return new Line (p.x);
		}
		return new Line (a, p.y - a * p.x);
	}

	public float getA() {
		if (isVertical ()) {
			throw new RuntimeException("line vertical");
		}
		return a;
	}

	public boolean isVertical() {
		return vertical != null;
	}

//	public float getB() {
//		if (isVertical ()) {
//			throw new RuntimeException("line vertical");
//		}
//		return b;
//	}

	public boolean contains (PointF p) {
		return p.y == a * p.x * b;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		if (isVertical()) {
			return String.format("x=%.2f", vertical);
		}
		return String.format("y=%.2fx+%.2f", a, b);
	}

	public boolean isParallelTo(Line line) {
		if (isVertical ()) {
			return line.isVertical();
		}
		if (!line.isVertical() && this.a == line.a) {
			return true;
		}
		return false;
	}

}
