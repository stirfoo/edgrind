/*
  Line2.java
  S. Edward Dolan
  Tuesday, September 26 2023
*/

package edgrind.geom;

import java.awt.Shape;
// 
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

public class Line2 {
    public double x1, y1, x2, y2;
    // 
    public Line2() {
        this(0, 0, 0, 0);
    }
    public Line2(Vec2 v1, Vec2 v2) {
        this(v1.x, v1.y, v2.x, v2.y);
    }
    public Line2(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    public String toString() {
        return String.format("Line2[%.4f %.4f %.4f %.4f]", x1, y1, x2, y2);
    }
    public Line2D toLine2D() {
        return new Line2D.Double(x1, y1, x2, y2);
    }
    public Vec2 p1() {
        return new Vec2(x1, y1);
    }
    public Vec2 p2() {
        return new Vec2(x2, y2);
    }
    /**
       @return a vector from this line's start point to end point
    */
    public Vec2 toVec2() {
        return new Vec2(x2 - x1, y2 - y1);
    }
    /**
       @return a Vec2 midway between this line's end points
    */
    public Vec2 midPoint() {
        return new Vec2((x1 + x2) / 2, (y1 + y2) / 2);
    }
    /**
       @return the length of the line segment
    */
    public double length() {
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        return Math.sqrt(dx * dx + dy * dy);
    }
    /**
       Find a point on this line given a scalar.

       0.0 would be the start point, 1.0 would be the end point.

       @param t the signed scalar
       @return a new Vec2
    */
    public Vec2 pointAtT(double t) {
        return new Vec2(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t);
    }
    public Shape xformShape(AffineTransform m) {
        return m.createTransformedShape(toLine2D());
    }
    public Rect2 getBBox(AffineTransform m) {
        if (m == null)
            m = new AffineTransform();
        Shape s = xformShape(m);
        return new Rect2(s.getBounds2D());
    }
}
