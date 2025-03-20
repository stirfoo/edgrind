/*
  Vec2.java
  S. Edward Dolan
  Sunday, September 17 2023
*/

package edgrind.geom;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
//
import edgrind.error.ZeroError;

/**
   A pair of doubles.

   A Vec2 may be used as:
   <ol>
   <li>A 2d point where x and y represent a location in 2d space.</li>
   <li>A 2d vector where x and y represent both direction and magnitude.</li>
   </ol>
 */
public class Vec2 {
    /**
       The value used when testing equalty with another Vec2. Initially set to
       1e-8.
     */
    public double EPSILON = 1e-8;
    /** The x component. */    
    public double x;
    /** The y component. */
    public double y;
    public Vec2() {
        this(0, 0);
    }
    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
       Create a vector from p1 to p2.
    */
    public Vec2(Vec2 p1, Vec2 p2) {
        this(p2.x - p1.x, p2.y - p1.y);
    }
    /**
       Create a vector from a Point2D.
     */
    public Vec2(Point2D p) {
        this(p.getX(), p.getY());
    }
    @Override
    public boolean equals(Object v) {
        if (v instanceof Vec2) {
            Vec2 vv = (Vec2)v;
            return Math.abs(x - vv.x) <= EPSILON
                && Math.abs(y - vv.y) <= EPSILON;
        }
        return false;
    }
    public Vec2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public Vec2 set(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    public Vec2 set(Point2D p) {
        this.x = p.getX();
        this.y = p.getY();
        return this;
    }
    @Override
    public String toString() {
        return String.format("Vec2 %+-15.8f %+-15.8f", x, y);
    }
    public Point2D toPoint2D() {
        return new Point2D.Double(x, y);
    }
    /**
       @return the length of the vector.
     */
    public double mag() {
        return Math.sqrt(x * x + y * y);
    }
    /**
       @return this normalized
    */
    public Vec2 norm() {
        double m = mag();
        if (m == 0)
            throw new ZeroError("Vec2 magnitude is zero");
        x /= m;
        y /= m;
        return this;
    }
    /**
       @return this with v added
    */
    public Vec2 add(Vec2 v) {
        x += v.x;
        y += v.y;
        return this;
    }
    /**
       @return this with v subtracted
    */
    public Vec2 sub(Vec2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }
    /**
       Subtract the components from this vector.
       @param x the x component
       @param y the y component
       @return this
     */
    public Vec2 sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    /**
       @return this scaled by s
    */
    public Vec2 mul(double s) {
        x *= s;
        y *= s;
        return this;
    }
    /**
       @return this negated
    */
    public Vec2 neg() {
        x = -x;
        y = -y;
        return this;
    }
    /**
       @return the dot product of this and v
    */
    public double dot(Vec2 v) {
        return x * v.x + y * v.y;
    }
    /**
       Find the normalized bisector of this and v.

       This is the same as adding 2 vectors and then normalizing the result.

       @return a new Vec2
    */
    public Vec2 bisector(Vec2 v) {
        return Vec2.add(this, v).norm();
    }
    /**
       Get the angle this vector forms with the horizontal (X+) axis.
       @return the angle in radians
       @todo rangle is a crap name (wtf does `rangle' even mean?)
    */
    public double rangle() {
        return Math.atan2(y, x);
    }
    /**
      Find the projection of this point onto the line thru a and b.

      @return a new Vec2
    */
    public Vec2 project(Vec2 a, Vec2 b) {
        Vec2 v = new Vec2(a, this);
        Vec2 u = new Vec2(a, b).norm();
        return Vec2.add(a, u.mul(v.dot(u)));
    }
    /**
      Find the projection of this point onto a line.
      @param l the line to project onto
      @return a new Vec2
    */
    public Vec2 project(Line2 l) {
        return project(l.p1(), l.p2());
    }
    /**
       Apply m to this point.
       
       @return a copy of this point transformed by m
    */
    public Vec2 xform(AffineTransform m) {
        return new Vec2(m.transform(toPoint2D(), null));
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    public static Vec2 norm(Vec2 v) {
        return new Vec2(v.x, v.y).norm();
    }
    public static Vec2 add(Vec2 v1, Vec2 v2) {
        return new Vec2(v1.x + v2.x, v1.y + v2.y);
    }
    public static Vec2 sub(Vec2 v1, Vec2 v2) {
        return new Vec2(v1.x - v2.x, v1.y - v2.y);
    }
    public static Vec2 mul(Vec2 v, double s) {
        return new Vec2(v.x * s, v.y * s);
    }
    public static Vec2 neg(Vec2 v) {
        return new Vec2(-v.x, -v.y);
    }
    public static Vec2 xform(Vec2 p, AffineTransform m) {
        return new Vec2(p.x, p.y).xform(m);
    }
    public static double distance(Vec2 p1, Vec2 p2) {
        return new Vec2(p1, p2).mag();
    }
}
