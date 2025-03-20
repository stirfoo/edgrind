/*
  AngleDim.java
  S. Edward Dolan
  Monday, September 25 2023
*/

package edgrind.sketch;

import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;
// 
import java.awt.Graphics2D;
//
import edgrind.Dict;
//
import edgrind.geom.*;
//
import edgrind.error.*;

/**
   Define an angular dimension.

   The dict specs are:
   
     name -- String, key name in the parent sketch's dict
    value -- double, angle in degrees
      pos -- Vec2, the label center coords in user space
    line1 -- Line2, first line to dimension
    line2 -- Line2, second line to dimension
   vertex -- Vec2, intersection of two line refs, if null, config() must solve
    quadV -- Vec2, point to the circle quadrant to dimension
   format -- String, "%.2f" for instance
 */
@SuppressWarnings("serial")
public class AngleDim extends SketchDim {
    AngleDim(SketchScene scene, Sketch sketch, String name) {
        this(scene, sketch, name, false);
    }
    AngleDim(SketchScene scene, Sketch sketch, String name,
             boolean readOnly) {
        this(new Dict("name", name,
                      "value", 45.0,
                      "pos", new Vec2(1, .5),
                      "line1", new Line2(0, 0, 1, 0),
                      "line2", new Line2(0, 0, 1, 1),
                      "vertex", null, // config() must solve
                      "quadV", new Vec2(1, 1).norm(),
                      "outside", false,
                      "format", SketchDim.FMT_ANG),
             scene,
             sketch,
             readOnly);
    }
    private AngleDim(Dict d, SketchScene scene, Sketch sketch,
                     boolean readOnly) {
        super(scene, sketch, readOnly);
        specs = (Dict)d.clone();
        config(d);
    }
    static double clamp(double x, double min, double max) {
        if (x < min)
            return min;
        if (x > max)
            return max;
        return x;
    }
    void config(Dict d) {
        super.config(d);
        Vec2 vertex = d.vecAt("vertex");
        Line2 l1 = d.lineAt("line1");
        Line2 l2 = d.lineAt("line2");
        if (vertex == null) {
            // find the intersection of the two ref lines
            vertex = new Vec2(0, 0);
            AlgoEnum e = Algo.xsect(l1, l2, vertex);
            if (e == AlgoEnum.NO_XSECT) {
                // System.out.println("l1:" + l1 + "\nl2:" + l2);
                throw new DimConfigError("angle dimension reference lines" +
                                         " do not intersect");
            }
        }
        Rect2 r = label.getBBox();
        Vec2 bbc = new Vec2(r.x + r.w / 2, r.y + r.h / 2);
        Vec2 labelV = new Vec2(vertex, bbc);
        double radius = labelV.mag();
        // guess the line vectors
        Vec2 v1 = new Vec2(l1.p1(), l1.p2()).norm();
        Vec2 v2 = new Vec2(l2.p1(), l2.p2()).norm();
        Vec2 quadV = Vec2.norm(d.vecAt("quadV"));
        // maybe reverse the line vectors so they point towards the quadrant
        if (v1.dot(quadV) <= 0)
            v1.neg();
        if (v2.dot(quadV) <= 0)
            v2.neg();
        // NOTE: v1 and v2 are correct here
        // angle bisector
        Vec2 bisectV = Vec2.add(v1, v2).norm();
        // bisector rotate 90 deg ccw
        Vec2 bisectV90 = new Vec2(-bisectV.y, bisectV.x);
        //
        // NOTE: v1, v2, bisectV, and bisectV90 are correct at this point
        // 
        Line2 lL = l1;
        Line2 rL = l2;
        Vec2 lV = v1;
        Vec2 rV = v2;
        if (bisectV90.dot(v1) < 0) {
            // v1 is between the bisector and its perpendicular which puts
            // v1 on the left of the bisector
            lL = l2;
            rL = l1;
            lV = v2;
            rV = v1;
        }
        // arrow point coordinates
        Vec2 lAp = Vec2.mul(lV, radius).add(vertex);
        Vec2 rAp = Vec2.mul(rV, radius).add(vertex);
        // find where the label lays
        labelV.norm();
        Vec2 lVperp = new Vec2(-lV.y, lV.x);
        Vec2 rVperp = new Vec2(rV.y, -rV.x);
        // leader arc rect
        Arc2D arc1 = new Arc2D.Double(Arc2D.OPEN);
        arc1.setFrame(vertex.x - radius, vertex.y - radius,
                      radius*2, radius*2);
        Arc2D arc2 = new Arc2D.Double(Arc2D.OPEN);
        arc2.setFrame(vertex.x - radius, vertex.y - radius,
                      radius*2, radius*2);
        /*
          When left and right are mentioned, they are relative to the
          intersection point of the two ref lines, looking in the direction of
          the quadV.
        */
        boolean outside = d.boolAt("outside");
        double arcLen = scene.getPixelSize(LEADER_LEN);
        double sweep = arcLen / radius;
        doReset();
        if (labelV.dot(lVperp) > 0) {           // label left of arrows
            if (outside) {
                // left leader
                arc1.setAngles(bbc.toPoint2D(), lAp.toPoint2D());
                AffineTransform m
                    = AffineTransform.getRotateInstance(-sweep, vertex.x,
                                                        vertex.y);
                // right leader
                arc2.setAngles(rAp.toPoint2D(), rAp.xform(m).toPoint2D());
                append(arc1, false);
                append(arc2, false);
            }
            else {
                // one arc leader from left arrow to center of label
                arc1.setAngles(bbc.toPoint2D(), rAp.toPoint2D());
                append(arc1, false);
            }
        }
        else if (labelV.dot(rVperp) > 0) {       // label right of arrows
            if (outside) {
                // right leader
                arc1.setAngles(rAp.toPoint2D(), bbc.toPoint2D());
                AffineTransform m
                    = AffineTransform.getRotateInstance(sweep, vertex.x,
                                                        vertex.y);
                // left leader
                arc2.setAngles(lAp.xform(m).toPoint2D(), lAp.toPoint2D());
                append(arc1, false);
                append(arc2, false);
            }
            else {
                // one arc leader from left arrow to center of label
                arc1.setAngles(lAp.toPoint2D(), bbc.toPoint2D());
                append(arc1, false);
            }
        }
        else {                  // label between the arrows
            if (outside) {
            }
            else {
            }
        }
        // config the arrows
        Vec2 dir1 = new Vec2(-lV.y, lV.x);
        Vec2 dir2 = new Vec2(rV.y, -rV.x);
        arrow1.config(new Dict("pos", lAp,
                               "dir", outside ? dir1.neg() : dir1));
        arrow2.config(new Dict("pos", rAp,
                               "dir", outside ? dir2.neg() : dir2));
        addExtensionLines(vertex, vertex, lAp, rAp);
    }
}
