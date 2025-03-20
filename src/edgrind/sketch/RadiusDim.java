/*
  RadiusDim.java
  S. Edward Dolan
  Monday, September 25 2023
*/

package edgrind.sketch;

import edgrind.Dict;
//
import edgrind.geom.*;

/**
   Define a radius dimension.

   The dict specs are:
   
   <dl>
   <dt>name</dt><dd>String, key name in the parent sketch's dict</dd>
   <dt>value</dt><dd>double, radius</dd>
   <dt>pos</dt><dd>Vec2, the label center coords in user space</dd>
   <dt>arc</dt><dd>Arc2, the reference arc being dimensioned</dd>
   <dt>outside</dt><dd>boolean, if true arrow points toward arc center</dd>
   <dt>format</dt><dd>String, "%.2f" for instance</dd>
   </dl>
 */
@SuppressWarnings("serial")
public class RadiusDim extends SketchDim {
    RadiusDim(SketchScene scene, Sketch sketch, String name) {
        this(scene, sketch, name, false);
    }
    RadiusDim(SketchScene scene, Sketch sketch, String name,
              boolean readOnly) {
        this(new Dict("name", name,
                      "value", .5,
                      "pos", new Vec2(3.5, 3.5),
                      "arc", new Arc2(0, 0, .5, 0, 90),
                      "outside", false,
                      "format", SketchDim.FMT_RAD),
             scene,
             sketch,
             readOnly);
        arrow2 = null;          // not used
    }
    private RadiusDim(Dict d, SketchScene scene, Sketch sketch,
                      boolean readOnly) {
        super(scene, sketch, readOnly);
        arrow2 = null;          // not used
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
        Vec2 pos = specs.vecAt("pos");
        Arc2 arc = specs.arcAt("arc");
        Vec2 arcCenter = arc.centerPt();
        boolean outside = specs.boolAt("outside");
        boolean labelOutside = new Vec2(arcCenter, pos).mag() > arc.r;
        boolean labelRight = pos.x > arc.cx;
        Rect2 r = label.getBBox();
        if (outside) {
            if (labelOutside) {
                double jogLen = scene.getPixelSize(JOG_LINE_LEN);
                Vec2 jogV, av;
                Vec2 jogSp, jogEp, ap;
                // jog horizontal line start point and direction
                if (labelRight) {
                    jogV = new Vec2(-1, 0).norm();
                    jogSp = new Vec2(pos.x - r.w / 2, pos.y);
                }
                else {
                    jogV = new Vec2(1, 0).norm();
                    jogSp = new Vec2(pos.x + r.w / 2, pos.y);
                }
                // jog horizontal line end point
                jogEp = Vec2.mul(jogV, jogLen).add(jogSp);
                ap = Algo.pointOnArc(jogEp, arcCenter, arc.r);
                av = new Vec2(jogEp, arcCenter);
                arrow1.config(new Dict("pos", ap, "dir", av));
                doReset();
                moveTo(jogSp.x, jogSp.y);
                lineTo(jogEp.x, jogEp.y);
                lineTo(ap.x, ap.y);
            }
            else {
            }
        }
        else {
            if (labelOutside) {
            }
            else {
            }
        }
    }
}
