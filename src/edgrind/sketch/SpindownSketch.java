/*
  SpindownSketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import edgrind.Dict;
//
import edgrind.geom.*;

/*
                 <-------------- l2 -------------->
                 
                 <----- l1 ----->                 
                                 .-----------------.     ^
                                 |                 |     |
           ^     .---------------|                 |     |
           |     |               |                 |     |
          d1     |               |                 |    d2
           |     |               |                 |     |
           v     '---------------|                 |     |
                                 |                 |     |
                                 '-----------------'     v
 */

@SuppressWarnings("serial")
public class SpindownSketch extends Sketch {
    static final int D1 = 0, D2 = 1;
    static final int L1 = 2, L2 = 3;
    public SpindownSketch(SketchScene scene) {
        // set some defaults
        super(new Dict("d1", .95,
                       "d2", 1.,
                       "l1", 1.375,
                       "l2", 1.5),
              scene);
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        // System.out.println("checkGeometry");
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double d1 = d.doubleAt("d1");
        double d2 = d.doubleAt("d2");
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        return d1 > 0
            && d2 > 0
            && l1 > 0
            && l2 > 0
            && d1 < d2
            && l1 < l2;
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.out.println("updateProfile");
        doReset();
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double d2 = specs.doubleAt("d2");
        double l2 = specs.doubleAt("l2");
        moveTo(0, 0);
        lineTo(0, d1 / 2);
        lineTo(l1, d1 / 2);
        lineTo(l1, d2 / 2);
        lineTo(l2, d2 / 2);
        lineTo(l2, 0);
        mirrorH();
        moveTo(l1, d1 / 2);
        lineTo(l1, -d1 / 2);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        // System.out.println("updateDims");
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double d2 = specs.doubleAt("d2");
        double l2 = specs.doubleAt("l2");
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // grind diameter
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double lx = -dlbb.w / 2 - dlg;
        double ly = 0;
        boolean outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > d1) {
            outside = true;
            ly = -d1 / 2 - ll;
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(0, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // grind length
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        lx = l1 / 2;
        ly = d2 / 2 + dlbb.h / 2 + dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l1) {
            outside = true;
            lx = -dlbb.w / 2 - ll;
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l1, d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // blank diameter
        dlbb = dims.get(D2).label.getBBox();
        a1bb = dims.get(D2).arrow1.getBBox();
        lx = l2 + dlbb.w / 2 + dlg;
        ly = 0;
        outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > d2) {
            outside = true;
            ly = -d2 / 2 - ll;
        }
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l2, d2 / 2),
                                     "ref2", new Vec2(l2, -d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // stick out
        dlbb = dims.get(L2).label.getBBox();
        a1bb = dims.get(L2).arrow1.getBBox();
        lx = l2 / 2;
        ly = d2 / 2 + dlbb.h * 1.5 + dlg * 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l2) {
            outside = true;
            lx = l2 + dlbb.w / 2 + ll;
        }
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l2, d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
}
