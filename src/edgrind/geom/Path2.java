/*
  Path2.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.geom;

import java.util.List;
import java.util.ArrayList;
//
import java.awt.Shape;
// 
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
   An extended java.awt.geom.Path2D.

   <p>
   This subclass adds an arcTo method, a list of arc start/end points.
   </p>
 */
@SuppressWarnings("serial")
public class Path2 extends Path2D.Double {
    /**
       Ordered array of arc start/end points.

       <p>
       Two points are added each time arcTo is called. This gets cleared when
       doReset() is called. This is needed because a PatherIterator on this
       Path will divide the arc into small line segments depending on the
       <em>flatness</em> parameter. So... the start/end points are lost in the
       soup.
       </p>
    */
    public List<Vec2> arcPts = new ArrayList<Vec2>();
    // can't override reset() because it's final...
    public void doReset() {
        reset();
        arcPts.clear();
    }
    /**
       Add an arc to the path.

       @param ex end x coordinate
       @param ey end y coordinate
       @param cx center x coordinate
       @param cy center y coordinate
       @param adir arc sweep direction
    */
    public void arcTo(double ex, double ey, double cx, double cy,
                      ArcDirection adir) {
        // last point added to this path
        Vec2 lp = new Vec2(getCurrentPoint());
        // arc center point
        Vec2 cp = new Vec2(cx, cy);
        // center point to start point
        Vec2 sv = new Vec2(cp, lp);
        // center point to end point
        Vec2 ev = new Vec2(cp, new Vec2(ex, ey));
        // parent circle bounding rectangle
        Rect2 r = new Rect2(cp, sv.mag() * 2, sv.mag() * 2);
        // angle to start point
        double a1 = -Math.toDegrees(sv.rangle());
        // angle to end point
        double a2 = -Math.toDegrees(ev.rangle());
        a1 %= 360.;
        a2 %= 360.;
        double sweep = 0;
        if (a1 == a2)
            sweep = 360;        // full circle
        else if (adir == ArcDirection.CLW)
            sweep = (a2 < a1 ? a2 + 360 : a2) - a1;
        else
            sweep = -((a1 < a2 ? a2 + 360 : a1) - a2);
        // System.out.println("a1:" + a1 + " a2:" + a2 + " sweep:" + sweep);
        append(new Arc2D.Double(r.toRectangle2D(), a1, sweep, Arc2D.OPEN),
               true);
        arcPts.add(lp);
        arcPts.add(Vec2.add(cp, ev));
    }
    public Shape xformShape(AffineTransform m) {
        return m.createTransformedShape(this);
    }
    public void append(Rect2 r) {
        append(new Rectangle2D.Double(r.x, r.y, r.w, r.h), false);
    }
    /**
       Mirror and append the current geometry about the horizontal axis.
    */
    public void mirrorH() {
        Shape s = AffineTransform.getScaleInstance(1, -1)
            .createTransformedShape(this);
        append(s, false);
    }
}
