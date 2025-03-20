/*
  SpindownSketch3.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import edgrind.Dict;
//
import edgrind.geom.*;

/**
   A spindown sketch with 3 diameters including the stock diameter.
 */
@SuppressWarnings("serial")
public class SpindownSketch3 extends Sketch {
    static final int D1 = 0, D2 = 1, D3 = 2;
    static final int L1 = 3, L2 = 4, L3 = 5;;
    public SpindownSketch3(SketchScene scene) {
        this(scene, new Dict("d1", .1, "l1", .1,
                             "d2", .2, "l2", .2,
                             "d3", .3, "l3", .3));
    }
    public SpindownSketch3(SketchScene scene, Dict d) {
        // set some defaults
        super(d, scene);
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        dims.add(new LinearDim(scene, this, "d3"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new LinearDim(scene, this, "l3"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        // System.out.println("checkGeometry");
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double d1 = d.doubleAt("d1");
        double d2 = d.doubleAt("d2");
        double d3 = d.doubleAt("d3");
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        double l3 = d.doubleAt("l3");
        return d1 > 0
            && d2 > 0
            && d3 > 0
            && l1 > 0
            && l2 > 0
            && l3 > 0
            && d1 < d2
            && d2 < d3
            && l1 < l2
            && l2 < l3;
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
        double d3 = specs.doubleAt("d3");
        double l3 = specs.doubleAt("l3");
        moveTo(0, 0);
        lineTo(0, d1 / 2);
        lineTo(l1, d1 / 2);
        lineTo(l1, d2 / 2);
        lineTo(l2, d2 / 2);
        lineTo(l2, d3 / 2);
        lineTo(l3, d3 / 2);
        lineTo(l3, 0);
        mirrorH();
        moveTo(l1, d1 / 2);
        lineTo(l1, -d1 / 2);
        moveTo(l2, d2 / 2);
        lineTo(l2, -d2 / 2);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        // System.out.println("updateDims");
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double d2 = specs.doubleAt("d2");
        double l2 = specs.doubleAt("l2");
        double d3 = specs.doubleAt("d3");
        double l3 = specs.doubleAt("l3");
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // 1st grind diameter
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
        // 1st grind length
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
        // 2nd diameter
        dlbb = dims.get(D2).label.getBBox();
        a1bb = dims.get(D2).arrow1.getBBox();
        lx = l1 + (l2 - l1) / 2;
        ly = -d2 / 2 - dlbb.h / 2 - ll;
        outside = true;
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(lx, d2 / 2),
                                     "ref2", new Vec2(lx, -d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // 2nd grind length
        dlbb = dims.get(L2).label.getBBox();
        a1bb = dims.get(L2).arrow1.getBBox();
        lx = l2 / 2;
        ly = d3 / 2 + dlbb.h / 2 + dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l2) {
            outside = true;
            lx = -dlbb.w / 2 - ll;
        }
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l2, d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // 3rd diameter
        dlbb = dims.get(D3).label.getBBox();
        a1bb = dims.get(D3).arrow1.getBBox();
        lx = l3 + dlbb.w / 2 + dlg;
        ly = 0;
        outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > d3) {
            outside = true;
            ly = -d3 / 2 - ll;
        }
        dims.get(D3).config(new Dict("name", "d3",
                                     "value", d3,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l3, d3 / 2),
                                     "ref2", new Vec2(l3, -d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // 3rd grind length
        dlbb = dims.get(L3).label.getBBox();
        a1bb = dims.get(L3).arrow1.getBBox();
        lx = l3 / 2;
        ly = d3 / 2 + dlbb.h * 1.5 + dlg * 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l3) {
            outside = true;
            lx = l3 + dlbb.w / 2 + ll;
        }
        dims.get(L3).config(new Dict("name", "l3",
                                     "value", l3,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d3 / 2),
                                     "ref2", new Vec2(l3, d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
}
