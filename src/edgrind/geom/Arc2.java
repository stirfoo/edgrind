/*
  Arc2.java
  S. Edward Dolan
  Tuesday, September 26 2023
*/

package edgrind.geom;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;

/**
  An extended java.awt.geom.Arc2D.Double.
 */
@SuppressWarnings("serial")
public class Arc2 extends Arc2D.Double {
    // ======================================================================
    // Instance Fields
    // ======================================================================
    public double cx, cy, r;
    // ======================================================================
    // Constructors
    // ======================================================================
    public Arc2() {
        super();
        cx = 0;
        cy = 0;
        r = 0;
    }
    /**
       @param x center coordinate
       @param y center coordinate
       @param r radius
       @param start angle in degrees
       @param sweep signed offset from start in degrees, + is cclw
    */
    public Arc2(double cx, double cy, double r, double start, double sweep) {
        super(cx - r, cy - r, r * 2, r * 2, start, sweep, Arc2D.OPEN);
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }
    // ======================================================================
    // Instance Methods
    // ======================================================================
    public String toString() {
        return String.format("Arc2[x:%.4f y:%.4f, r:%.4f, sa:%f, ea:%f]",
                             cx, cy, r, startAngle(), endAngle());
    }
    public Vec2 centerPt() {
        return new Vec2(cx, cy);
    }
    public double startAngle() {
        return start;
    }
    public double endAngle() {
        return (start + extent) % 360;
    }
    public double sweepAngle() {
        return endAngle() - startAngle();
    }
    public Vec2 startPt() {
        double rads = Math.toRadians(start);
        return new Vec2(cx + Math.cos(rads) * r, cy + Math.sin(rads) * r);
    }
    public Vec2 endPt() {
        double rads = Math.toRadians(endAngle());
        return new Vec2(cx + Math.cos(rads) * r, cy + Math.sin(rads) * r);
    }
    public Vec2 startAngleVec() {
        return new Vec2(centerPt(), startPt()).norm();
    }
    public Vec2 endAngleVec() {
        return new Vec2(centerPt(), endPt()).norm();
    }
    public Vec2 bisector() throws Exception  {
        if (Math.abs(extent) >= 360)
            throw new Exception("Arc2 > 360 deg has no bisector");
        double a = Math.toRadians((start + extent) / 2);
        return new Vec2(Math.cos(a), Math.sin(a));
    }
    public Shape inverted() {
        return getXformedShape(new AffineTransform());
    }
    /**
       Apply m to this arc.

       The y axis is inverted so the arc is drawn as if it were in a
       right-handed cartesian coordinate system.

       @return this
     */
    public Shape getXformedShape(AffineTransform m) {
        AffineTransform m2 = new AffineTransform(m);
        m2.concatenate(AffineTransform.getTranslateInstance(cx, cy));
        m2.scale(1, -1);
        m2.concatenate(AffineTransform.getTranslateInstance(-cx, -cy));
        return m2.createTransformedShape(this);

    }
    /**
       Find the bounding box for this arc in user space.

       @param m the matrix to apply, if null, return this unmodified
       
       @return this
     */
    public Rect2 getBBox(AffineTransform m) {
        if (m == null)
            m = new AffineTransform();
        Shape s = getXformedShape(m);
        return new Rect2(s.getBounds2D());
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    public static Arc2 fromCircle(double xc, double yc, double diameter) {
        return new Arc2(xc, yc, diameter / 2, 0, 360);
    }
    public static Arc2 fromAngles(double a1, double a2, double r,
                                  boolean cclw) {
        if (a1 == a2)
            return fromCircle(0, 0, r * 2);
        a1 %= 360.0;
        a2 %= 360.0;
        if (cclw)
            return new Arc2(0, 0, r, a1, (a2 < a1 ? a2 + 360 : a2) - a1);
        else
            return new Arc2(0, 0, r, a1, -((a1 < a2 ? a1 + 360 : a1) - a1));
    }
    public static Arc2 fromAngles(double a1, double a2, double r) {
        return fromAngles(a1, a2, r, true);
    }
    public static Arc2 fromVectors(Vec2 v1, Vec2 v2, double r, boolean cclw) {
        double a1 = Math.toDegrees(Math.atan2(v1.y, v1.x));
        double a2 = Math.toDegrees(Math.atan2(v2.y, v2.x));
        return Arc2.fromAngles(a1, a2, r, cclw);
    }
    public static Arc2 fromVectors(Vec2 v1, Vec2 v2, double r) {
        return fromVectors(v1, v2, r, true);
    }
}
