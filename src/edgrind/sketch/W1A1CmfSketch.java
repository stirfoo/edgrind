/*
  W1A1CmfSketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.Shape;
// 
import java.awt.geom.AffineTransform;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/*
               <--l1--->
                .-----.   
               /       \  ^
               |-------|  |
               |       |  |
               |       |  |
               |       |  |
               |       |  d1
          |    |       |  |
          |    |       |  |
          v    |       |  |
               |-------|  |
               |       |  |
               \       /  v
                '-----'   
          ^  --->     <---- l2
          |
          |
          l3 (bond height)
 */

/**
   A 1A1 wheel with 45 deg chamfers sketch.
 */
@SuppressWarnings("serial")
public class W1A1CmfSketch extends WheelSketch {
    protected static final int D1 = 0;
    protected static final int L1 = 1, L2 = 2, L3 = 3;
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 6,     // diameter
                   "l1", .5,    // width
                   "l2", .25,   // bond height
                   "l3", .331); // flat width
    public W1A1CmfSketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W1A1CmfSketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W1A1CmfSketch(Dict d, SketchScene scene, boolean mirror)
    {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new LinearDim(scene, this, "l3"));
    }
    // copy
    static public Dict getDefaultSpecs() {
        return (Dict)DEFAULT_SPECS.clone();
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double d1 = d.doubleAt("d1");
        double wr = d1 / 2;
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        double l3 = d.doubleAt("l3");
        double hr = holeDia / 2;
        double cmf = (l1 - l3) / 2;
        return
            // wheel diameter big enough 
            wr > hr + cmf &&
            // flat width less than the wheel width
            l3 < l1 &&
            // bond height bigger than the chamfer
            l2 > (l1 - l3) / 2 &&
            // bond height smaller than the hole dia
            l2 < wr - hr;
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.out.println("W1A1CmfSketch.updateProfile()");
        doReset();
        double d1 = specs.doubleAt("d1");
        double r = d1 / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double cmf = (l1 - l3) / 2;
        moveTo(0, holeDia / 2);
        lineTo(0, r - cmf);
        lineTo(cmf, r);
        lineTo(l1 - cmf, r);
        lineTo(l1, r - cmf);
        lineTo(l1, holeDia / 2);
        closePath();
        moveTo(0, r - l2);
        lineTo(l1, r - l2);
        mirrorH();
        if (mirror) {
            Shape s = AffineTransform.getScaleInstance(-1, 1)
                .createTransformedShape(this);
            doReset();
            append(s, false);
        }
        if (zlen > 0)
            super.updateZLenProfile(l1);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        if (zlen != 0)
            return;
        double x = mirror ? -1 : 1;
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double cmf = (l1 - l3) / 2;
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // d1 (diameter, right, center or top)
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double dlx = l1 + dlbb.w * .5 + dlg;
        double dly = 0;
        if (a1bb.h * 2 + dlbb.h + dlg > d1) {
            outside = true;
            dly = d1 / 2 + ll; // above wheel
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2((l1 - cmf) * x,
                                                      d1 / 2),
                                     "ref2", new Vec2((l1 - cmf) * x,
                                                      -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // (l1, width, top, center or left)
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        dlx = l1 / 2;           // centered
        dly = d1 / 2 + dlbb.h * .5 + dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l1) {
            outside = true;
            dlx = -dlbb.w * .5 - ll; // left of wheel
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(0, d1 / 2 - cmf),
                                     "ref2", new Vec2(l1 * x, d1 / 2 - cmf),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // (l2, bond height, left of wheel, center or above)
        a1bb = dims.get(L2).arrow1.getBBox();
        dlbb = dims.get(L2).label.getBBox();
        dlx = -dlbb.w * .5 - dlg;
        dly = -d1 / 2 + l2 / 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.h + dlg > l2) {
            outside = true;
            dly = -d1 / 2 + l2 + dlbb.h / 2 + ll; // above
        }
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(cmf * x, -d1 / 2),
                                     "ref2", new Vec2(0, -d1 / 2 + l2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "vertical"));
        // (l3, flat width, bottom, center or right)
        a1bb = dims.get(L3).arrow1.getBBox();
        dlbb = dims.get(L3).label.getBBox();
        dlx = l1 / 2;           // centered
        dly = -d1 / 2 - dlbb.h * .5 - dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l3) {
            outside = true;
            dlx = l1 + dlbb.w * .5 + ll; // right of wheel
        }
        dims.get(L3).config(new Dict("name", "l3",
                                     "value", l3,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(cmf * x, -d1 / 2),
                                     "ref2", new Vec2((l3 + cmf) * x, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
}
