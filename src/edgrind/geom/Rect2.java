/*
  Rect2.java
  S. Edward Dolan
  Tuesday, September 26 2023
*/

package edgrind.geom;

import java.awt.Shape;
// 
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
  An extended java.awt.geom.Rectangle2D.Double.

  This rectangle assumes the origin is at the bottom left.
  
  <pre>
                         <--- width --->
                         .-------------.   ^
                         |             |   |
                         |             | height
                         |             |   |
                x,y ---> o-------------'   v
  </pre>
 */
public class Rect2 {
    /**
       Create from origin, width, and height.

       This rectangle will be normalized.
    */
    public double x, y, w, h;
    public Rect2() {
        this(0, 0, 0, 0);
    }
    public Rect2(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        normalize();
    }
    /**
       Create from two opposite corner vertices.

       This rectangle will be normalized.
    */
    public Rect2(Vec2 v1, Vec2 v2) {
        this(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y),
             Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
        normalize();
    }
    /**
       Create from center vertex, width, and height.

       This rectangle will be normalized.
    */
    public Rect2(Vec2 center, double w, double h) {
        this(center.x - w / 2, center.y - h / 2, w, h);
        normalize();
    }
    /**
       Create from a Rectangle2D.

       This rectangle will be normalized.
    */
    public Rect2(Rectangle2D r) {
        this(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        normalize();
    }
    public boolean isEmpty() {
        return w == 0 && h == 0;
    }
    public Rect2 set(Rectangle2D r) {
        x = r.getX();
        y = r.getY();
        w = r.getWidth();
        h = r.getHeight();
        normalize();
        return this;
    }
    public String toString() {
        return String.format("Rect2[%f, %f, %f, %f]", x, y, w, h);
    }
    /**
       Return a new Rectangle2D with this Rect2 x, y, w, and h.
    */
    public Rectangle2D toRectangle2D() {
        return new Rectangle2D.Double(x, y, w, h);
    }
    /**
       @return this rectangle's center coordinates
    */
    public Vec2 center() {
        return new Vec2(x + w / 2, y + h / 2);
    }
    /**
       Move this rectangle's center coordinates to the new values.
    */
    public Rect2 setCenter(double cx, double cy) {
        x = cx - w / 2;
        y = cy - h / 2;
        normalize();
        return this;
    }
    /**
       @return the minimum x coordinate
    */
    public double left() {
        return x;
    }
    /**
       @return the maximum x coordinate
    */
    public double right() {
        return x + w;
    }
    /**
       @return the maximum y coordinate
    */
    public double top() {
        return y + h;
    }
    /**
       @return the minimum y coordinate
    */
    public double bottom() {
        return y;
    }
    /**
       @return a new vertex with the mininum x and y coordinates.
    */
    public Vec2 bottomLeft() {
        return new Vec2(x, y);
    }
    /**
       @return a new vertex with the maximum x and minimum y coordinates.
    */
    public Vec2 bottomRight() {
        return new Vec2(x + w, y);
    }
    /**
       @return a new vertex with the minumum x and maximum y coordinates.
    */
    public Vec2 topLeft() {
        return new Vec2(x, y + h);
    }
    /**
       @return a new vertex with the maximum x and y coordinates.
    */
    public Vec2 topRight() {
        return new Vec2(x + w, y + h);
    }
    /**
       Make sure the width and height are positive.

       @return this
    */
    public Rect2 normalize() {
        if (w < 0) {
            x -= w;
            w = -w;
        }
        if (h < 0) {
            y -= h;
            h = -h;
        }
        return this;
    }
    /**
       Adjust the sides of this rectangle. The width and height will be
       clamped to >= 0. The resulting rectangle is normalized.
       
       @param left the value to add to the minumum x coordinate
       @param right the value to add to the maximum x coordinate
       @param top the value to add to the maximum y coordinate
       @param bottom the value to add to minumum y coordinate
       
       @return this rect adjusted
    */
    public Rect2 adjust(double left, double right, double top, double bottom) {
        double r = this.right() + right;
        double t = this.top() + top;
        x += left;
        y += bottom;
        w = Math.max(r - x, 0);
        h = Math.max(t - y, 0);
        normalize();
        return this;
    }
    public Rect2 adjusted(double left, double right, double top,
                          double bottom) {
        return new Rect2(x, y, w, h).adjust(left, right, top, bottom);
    }
    /**
       Return the transformed Shape of this rectangle.
    */
    public Shape xformShape(AffineTransform m) {
        Shape s = m.createTransformedShape(toRectangle2D());
        // PathIterator pi = s.getPathIterator(null);
        // double p[] = new double[2];
        // int i = 0;
        // while (!pi.isDone()) {
        //     pi.currentSegment(p);
        //     System.out.format("[%2d] [%f, %f]\n", i++, p[0], p[1]);
        //     pi.next();
        // }
        return s;
    }
    /**
       Find the bounding box for this rectangle in user space.

       @param m the matrix to apply, if null, return this unmodified
       
       @return this
    */
    public Rect2 getBBox(AffineTransform m) {
        if (m == null)
            return this;
        Shape s = xformShape(m);
        return new Rect2(s.getBounds2D());
    }
    /**
       Find the union of this rect and r.
       
       <p>
       TODO: Implement this myself.
       </p>
       
       @return this
     */
    public Rect2 add(Rect2 r) {
        Rectangle2D r1 = new Rectangle2D.Double(x, y, w, h);
        r1.add(r.toRectangle2D());
        return set(r1);
    }
}
