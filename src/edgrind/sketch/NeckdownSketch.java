/*
  NeckdownSketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.geom.Point2D;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/*
              <-- l3 ------------------------------------>
              <-- l2 ------------------>
              <-- l1 -->               .-----------------.
              .--------.               |        ^        |
              |    ^   |---------------|        |        |
              |    |   |      ^        |        |        |
              |   d1   |      d2       |       d3        |
              |    |   |      v        |        |        |
              |    v   |---------------|        |        |
              `--------'               |        V        |
                                       '-----------------'
 */

@SuppressWarnings("serial")
public class NeckdownSketch extends Sketch {
    static final int D1 = 0, D2 = 1, D3 = 2;
    static final int L1 = 3, L2 = 4, L3 = 5;
    NeckdownSketch(SketchScene scene) {
        // set some defaults
        super(new Dict("d1", .5, "d2", .375, "d3", .625,
                       "l1", 1.03, "l2", 2., "l3", 2.2),
              scene);
        // must be added in the order that further methods expect them
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        dims.add(new LinearDim(scene, this, "d3"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new LinearDim(scene, this, "l3"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        return d.doubleAt("d1") <= d.doubleAt("d3")
            && d.doubleAt("d2") < d.doubleAt("d1")
            && d.doubleAt("l2") > d.doubleAt("l1")
            && d.doubleAt("l3") > d.doubleAt("l2");
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        doReset();
        double d1r = specs.doubleAt("d1") / 2;
        double d2r = specs.doubleAt("d2") / 2;
        double d3r = specs.doubleAt("d3") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        // start at bottom left, move clock-wise
        moveTo(0, -d1r);
        lineTo(0, d1r);
        lineTo(l1, d1r);
        lineTo(l1, d2r);
        lineTo(l2, d2r);
        lineTo(l2, d3r);
        lineTo(l3, d3r);
        lineTo(l3, -d3r);
        lineTo(l2, -d3r);
        lineTo(l2, -d2r);
        lineTo(l1, -d2r);
        lineTo(l1, -d1r);
        lineTo(0, -d1r);
        closePath();
        moveTo(l1, -d2r);
        lineTo(l1, d2r);
        moveTo(l2, -d2r);
        lineTo(l2, d2r);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double d3 = specs.doubleAt("d3");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        // d1
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double w = dlbb.w * .6;
        double lx = -w; //dims[D2].label.getBBox()().getX() - w;g
        double ly = 0;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d1) {
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
        // d2
        outside = false;
        dlbb = dims.get(D2).label.getBBox();
        lx = (l2 - l1) / 2 + l1;
        ly = -d3 / 2 - dlbb.h * 1.5;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d2)
            outside = true;
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l1, d2 / 2),
                                     "ref2", new Vec2(l1, -d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // d3
        outside = false;
        dlbb = dims.get(D3).label.getBBox();
        lx = l3 + dlbb.w * .6;
        ly = 0;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d3) {
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
        // l1
        outside = false;
        dlbb = dims.get(L1).label.getBBox();
        a1bb = dims.get(L1).arrow1.getBBox();
        double h = dlbb.h * 1.5;
        lx = l1 / 2;
        ly = d3 / 2 + h;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l1) {
            outside = true;
            lx = -ll;
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l1, d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // l2
        outside = false;
        dlbb = dims.get(L2).label.getBBox();
        a1bb = dims.get(L2).arrow1.getBBox();
        lx = l2 / 2;
        ly = d3 / 2 + h * 2;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l2) {
            outside = true;
            lx = -ll;
        }
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d3 / 2),
                                     "ref2", new Vec2(l2, d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // l3
        outside = false;
        dlbb = dims.get(L3).label.getBBox();
        a1bb = dims.get(L3).arrow1.getBBox();
        lx = l3 / 2;
        ly = d3 / 2 + h * 3;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l3) {
            outside = true;
            lx = -ll;
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
