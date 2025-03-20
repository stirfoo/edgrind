/*
  SK50Sketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.Color;
// 
import java.util.List;
import java.util.Arrays;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/*
           <------ l2 ---->
           <-l1->
           .-----.
       ^   |     |
       |   |     |'-.            ^
       |   |     |   '-.         |
       |   |     |      '-.      |
       |   |     |        |  ^   |
      d1   |     |        |  d3  d2
       |   |     |        |  v   |
       |   |     |      .-'      |
       |   |     |   .-'         |
       |   |     |.-'            v
       v   |     |
           '-----'
 */

/**
   A GDS Cobra SK50 collet chuck sketch.
 */
@SuppressWarnings("serial")
public class SK50Sketch extends Sketch {
    static final int D1 = 0, D2 = 1, D3 = 2;
    static final int L1 = 3, L2 = 4;
    public SK50Sketch(SketchScene scene) {
        // default dims are for an SK50 3718E L90 400007060
        this(new Dict("d1", 100/25.4, "d2", 70/25.4, "d3", 52/25.4,
                      "l1", 16/25.4 /*.715*/, "l2", 60/25.4),
             scene);
    }
    public SK50Sketch(Dict d, SketchScene scene) {
        // default dims are for an SK50 3718E L90 400007060
        super(d, scene);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        dims.add(new LinearDim(scene, this, "d3"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        return d.doubleAt("d3") < d.doubleAt("d2")
            && d.doubleAt("d2") < d.doubleAt("d1")
            && d.doubleAt("l2") > d.doubleAt("l1");
    }
    @Override
    protected void updateProfile() {
        doReset();
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double d3 = specs.doubleAt("d3");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        append(new Rect2(0, -d1 / 2, l1, d1));
        moveTo(l1, d2 / 2);
        lineTo(l2, d3 / 2);
        lineTo(l2, -d3 / 2);
        lineTo(l1, -d2 / 2);
    }
    @Override
    protected void updateDims() {
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double d3 = specs.doubleAt("d3");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // d1
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double lx = -dlbb.w - dlg;
        double ly = 0;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d1) {
            outside = true;
            ly = -d1 / 2 - dlbb.h * .5 - ll;
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
        dlbb = dims.get(D2).label.getBBox();
        a1bb = dims.get(D2).arrow1.getBBox();
        lx = l2 + dlbb.w * 1.7;
        ly = 0;
        outside = false;
        if (a1bb.h * 2 + dlbb.h > d2) {
            outside = true;
            ly = -d2 / 2 - dlbb.h * .5 - ll;
        }
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l1, d2 / 2),
                                     "ref2", new Vec2(l1, -d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // d3
        dlbb = dims.get(D3).label.getBBox();
        a1bb = dims.get(D3).arrow1.getBBox();
        lx = l2 + dlbb.w * .6;
        ly = 0;
        outside = false;
        if (a1bb.h * 2 + dlbb.h > d3) {
            outside = true;
            ly = -d3 / 2 - dlbb.h * .5 - ll;
        }
        dims.get(D3).config(new Dict("name", "d3",
                                     "value", d3,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l2, d3 / 2),
                                     "ref2", new Vec2(l2, -d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // l1
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        lx = l1 / 2;
        ly = d1 / 2 + dlbb.h * 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l1) {
            outside = true;
            lx = -dlbb.w * .5 - ll;
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
        a1bb = dims.get(L2).arrow1.getBBox();
        dlbb = dims.get(L2).label.getBBox();
        lx = l2 / 2;
        ly = d1 / 2 + dlbb.h * 4; // dependent on above ly
        outside = false;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l2) {
            outside = true;
            lx = -dlbb.w * .5 - ll;
        }
        dims.get(L2).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l2, d3 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
    @Override
    public Model getModel() {
        Model model = new Model(Color.gray);
        double r1 = specs.doubleAt("d1") / 2;
        double r2 = specs.doubleAt("d2") / 2;
        double r3 = specs.doubleAt("d3") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        List<Vec2> pts = Arrays.asList(new Vec2(0, 0),
                                       new Vec2(0, r1),
                                       new Vec2(l1, r1),
                                       new Vec2(l1, r2),
                                       new Vec2(l2, r3),
                                       new Vec2(l2, 0));
        model.add(Mesh.revolvePolyline(pts));
        return model;
    }
}
