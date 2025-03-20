/*
  WeldonFlatSketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import edgrind.Dict;
//
import edgrind.geom.*;

/*
                 <------- l1 --------> 
                           l2 --->       <---
                 .--------------.          .------.
           ^     |               \________/       |     
           |     |                                |   ^
           |     |                                |   |
          d1     |                                |  d2
           |     |                                |   |
           |     |                                |   |
           v     |                                |   v  
                 '--------------------------------'
                 <-------------- l3 -------------->
 */

/**
   Standard Weldon Flat sketch.
 */

@SuppressWarnings("serial")
public class WeldonFlatSketch extends Sketch {
    static final int D1 = 0, D2 = 1;
    static final int L1 = 2, L2 = 3, L3 = 4;
    public WeldonFlatSketch(SketchScene scene) {
        // standard weldon flat dimensions for a 1/2" shank
        super(new Dict("d1", .5,
                       "d2", .435,
                       "l1", .8906,
                       "l2", .331,
                       "l3", 1.5),
              scene);
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new LinearDim(scene, this, "l3"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double d1 = d.doubleAt("d1");
        double d2 = d.doubleAt("d2");
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        double l3 = d.doubleAt("l3");
        // TODO: MOAR checks so the flat chamfers don't exceed the oal
        return d2 > 0
            && d2 < d1
            && l1 > 0
            && l1 < l3
            && l2 > 0
            && l1 > l2 / 2;
    }
    @Override
    protected void updateProfile() {
        doReset();
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double sr = d1 / 2;      // shank radius
        double x1 = l1 - l2 / 2; // left corner of flat
        double x2 = l1 + l2 / 2; // right ...
        double fd = d1 - d2;     // flat depth
        moveTo(0, -sr);
        lineTo(0, sr);
        lineTo(x1 - fd, sr);
        lineTo(x1, sr - fd);
        lineTo(x2, sr - fd);
        lineTo(x2 + fd, sr);
        lineTo(l3, sr);
        lineTo(l3, -sr);
        closePath();
    }
    @Override
    protected void updateDims() {
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // shank diameter
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double lx = -dlbb.w / 2 - dlg;
        double ly = 0;
        boolean outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > d1) {
            outside = true;
            ly = -d1 / 2 - dlbb.h / 2 - ll;
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(0, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // bottom of shank to flat
        dlbb = dims.get(D2).label.getBBox();
        a1bb = dims.get(D2).arrow1.getBBox();
        lx = l3 + dlbb.w / 2 + dlg;
        ly = -(d1 - d2) / 2;
        outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > d2) {
            outside = true;
            ly = -d1 / 2 - dlbb.h / 2 - ll;
        }
        double x2 = l1 + l2 / 2;
        double fy = d1 / 2 - (d1 - d2);
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(x2, fy),
                                     "ref2", new Vec2(l3, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // distance to center of flat
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        lx = l1 / 2;
        ly = d1 / 2 + dlbb.h * 1.5 + dlg * 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l1) {
            outside = true;
            lx = -dlbb.w / 2 - ll;
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l1, fy),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // flat width
        double x1 = l1 - l2 / 2;
        a1bb = dims.get(L2).arrow1.getBBox();
        dlbb = dims.get(L2).label.getBBox();
        lx = x1 - dlbb.w / 2 - ll;
        ly = d1 / 2 + dlbb.h / 2 + dlg;
        outside = true;         // always outside
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(x1, fy),
                                     "ref2", new Vec2(x2, fy),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // stick out
        dlbb = dims.get(L3).label.getBBox();
        a1bb = dims.get(L3).arrow1.getBBox();
        lx = l3 / 2;
        ly = -d1 / 2 - dlbb.h / 2 - dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l3) {
            outside = true;
            lx = -dlbb.w / 2 - ll;
        }
        dims.get(L3).config(new Dict("name", "l3",
                                     "value", l3,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, -d1 / 2),
                                     "ref2", new Vec2(l3, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
}
