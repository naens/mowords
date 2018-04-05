package com.naens.ui;

import java.util.LinkedList;
import java.util.List;

import android.graphics.PointF;

import com.naens.ui.DrawView.PenMode;

public abstract class DrawingAction {

	protected PenMode penMode;

	static class Clean extends DrawingAction {

		@Override
		public void shift(int x, int y) {
		}
	}

	static class Point extends DrawingAction {
		private float x;
		private float y;

		public Point(PointF point, PenMode penMode) {
			x = point.x;
			y = point.y;
			this.penMode = penMode;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		@Override
		public void draw(android.graphics.Path path) {
			path.moveTo(x - (float) 0.005, y - (float) 0.005);
			path.lineTo(x + (float) 0.005, y + (float) 0.005);
		}

		public PenMode getPenMode() {
			return penMode;
		}

		@Override
		public void shift(int x, int y) {
			this.x += x;
			this.y += y;
		}
	}

	static class Path extends DrawingAction {

		private List<PathObject> objects = new LinkedList<PathObject>();
		private float length = 0;

		public Path(PenMode penMode) {
			this.penMode = penMode;
		}

		public PenMode getPenMode() {
			return penMode;
		}

		public void addLine(PointF p1, PointF p2) {
			objects.add(new Line(p1, p2));
			length += pointDistance(p1, p2);
		}

		public void addQuad(PointF p1, PointF p2, PointF c) {
			objects.add(new Quad(p1, p2, c));
			length += arcLengthByIntegral(p1, p2, c);
		}

		public float getLength() {
			return length;
		}

		private abstract class PathObject {
			public abstract void draw(android.graphics.Path path);

			public abstract void shift(int x, int y);
		}

		class Line extends PathObject {

			private float p1x;
			private float p1y;
			private float p2x;
			private float p2y;

			private Line(PointF p1, PointF p2) {
				this.p1x = p1.x;
				this.p1y = p1.y;
				this.p2x = p2.x;
				this.p2y = p2.y;
			}

			public float getP1x() {
				return p1x;
			}

			public float getP1y() {
				return p1y;
			}

			public float getP2x() {
				return p2x;
			}

			public float getP2y() {
				return p2y;
			}

			@Override
			public void draw(android.graphics.Path path) {
				path.moveTo(p1x, p1y);
				path.lineTo(p2x, p2y);
			}

			@Override
			public void shift(int x, int y) {
				p1x += x;
				p1y += y;
				p2x += x;
				p2y += y;
			}
		}

		class Quad extends PathObject {

			private float p1x;
			private float p1y;
			private float p2x;
			private float p2y;
			private float cx;
			private float cy;

			private Quad(PointF p1, PointF p2, PointF c) {
				this.p1x = p1.x;
				this.p1y = p1.y;
				this.p2x = p2.x;
				this.p2y = p2.y;
				this.cx = c.x;
				this.cy = c.y;
			}

			public float getP1x() {
				return p1x;
			}

			public float getP1y() {
				return p1y;
			}

			public float getP2x() {
				return p2x;
			}

			public float getP2y() {
				return p2y;
			}

			public float getCx() {
				return cx;
			}

			public float getCy() {
				return cy;
			}

			@Override
			public void draw(android.graphics.Path path) {
				path.moveTo(p1x, p1y);
				path.quadTo(cx, cy, p2x, p2y);
			}

			@Override
			public void shift(int x, int y) {
				p1x += x;
				p1y += y;
				p2x += x;
				p2y += y;
				cx += x;
				cy += y;
			}
		}

		@Override
		public void draw(android.graphics.Path path) {
			for (PathObject object : objects) {
				object.draw(path);
			}
		}

		@Override
		public void shift(int x, int y) {
			for (PathObject object : objects) {
				object.shift (x, y);
			}
		}
	}

	private static double pointDistance(PointF p1, PointF p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	private static double arcLengthByIntegral(PointF point1, PointF point2, PointF controlPoint) {
		float ax = point1.x - 2 * controlPoint.x + point2.x;
		float ay = point1.y - 2 * controlPoint.y + point2.y;
		float bx = 2 * controlPoint.x - 2 * point1.x;
		float by = 2 * controlPoint.y - 2 * point1.y;

		double a = 4 * (ax * ax + ay * ay);
		double b = 4 * (ax * bx + ay * by);
		double c = bx * bx + by * by;

		double abc = 2 * Math.sqrt(a + b + c);
		double a2 = Math.sqrt(a);
		double a32 = 2 * a * a2;
		double c2 = 2 * Math.sqrt(c);
		double ba = b / a2;

		return (a32 * abc + a2 * b * (abc - c2) + (4 * c * a - b * b) * Math.log((2 * a2 + ba + abc) / (ba + c2)))
				/ (4 * a32);
	}

	public void draw(android.graphics.Path path) {
	}

	public PenMode getPenMode() {
		return penMode;
	}

	public abstract void shift(int x, int y);
}
