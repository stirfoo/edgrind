/*
  WheelAdapterSketch.java
  S. Edward Dolan
  Monday, September 18 2023
*/

package edgrind.sketch;

import java.util.Arrays;
import java.util.List;
// import java.util.ArrayList;
// 
import java.awt.Shape;
// 
import java.awt.geom.AffineTransform;
//
import edgrind.Dict;
// 
import edgrind.geom.*;
//
import edgrind.Util;

/*
  Base on the Walter NCT Adapter found here:
  
  https://toolroom.solutions/wp-content/uploads/2019/03/Toolroom-Solutions-GDS-grinding-wheel-adapters-for-WALTER-tool-grinders.pdf


                <-----------l3------->
                <--------l2------>
                <-----l1---->
     ----------------------  .---.
       ^                     |   |     <--------l4------->
       |        .------------|   |----.  ---------------------------        
       |   ^    |            |   |    |                           ^
       |   |    |            |   |    |                           |
       |   |    |            |   |    |------------------.        |
       |   |    |            |   |    |                  |   ^    |
       d2  d1   |            |   |    |                  |   d4   d3
       |   |    |            |   |    |                  |   v    |
       |   |    |            |   |    |------------------'        |
       |   |    |            |   |    |                           |
       |   v    |            |   |    |                           v
       |        '------------|   |----'  ---------------------------
       v                     |   |
     ----------------------  '---'
 */

@SuppressWarnings("serial")
public class WheelAdapterSketch extends Sketch {
    static final int D4 = 0, L4 = 1;
    public WheelAdapterSketch(SketchScene scene, boolean mirror)
        {
        // default to a 40mm journal length (300101112)
        super(new Dict("d1", 50 / 25.4,
                       "d2", 77 / 25.4,
                       "d3", 60 / 25.4,
                       "d4", 31.75 / 25.4,
                       "l1", 1.544,
                       "l2", 1.777,
                       "l3", 55 / 25.4,
                       "l4", 40 / 25.4),
              scene,
              mirror);
        dims.add(new LinearDim(scene, this, "d4"));
        dims.add(new LinearDim(scene, this, "l4"));
    }
    public WheelAdapterSketch(Dict d, SketchScene scene, boolean mirror)
        {
        super(d, scene, mirror);
        dims.add(new LinearDim(scene, this, "d4"));
        dims.add(new LinearDim(scene, this, "l4"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        return d.doubleAt("d4") > 0 && d.doubleAt("l4") > 0;
    }
    void updateProfile() {
        doReset();
        double r1 = specs.doubleAt("d1") / 2;
        double r2 = specs.doubleAt("d2") / 2;
        double r3 = specs.doubleAt("d3") / 2;
        double r4 = specs.doubleAt("d4") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double l4 = specs.doubleAt("l4");
        // left rect
        append(new Rect2(0, -r1, l1, r1*2));
        // 1st flange rect
        append(new Rect2(l1, -r2, l2-l1, r2*2));
        // 2nd flange rect
        append(new Rect2(l2, -r3, l3-l2, r3*2));
        // journal rect
        append(new Rect2(l3, -r4, l4, r4*2));
        if (mirror) {
            Shape s = AffineTransform.getScaleInstance(-1, 1)
                .createTransformedShape(this);
            doReset();
            append(s, false);
        }
    }
    void updateDims() {
        double d3 = specs.doubleAt("d3");
        double d4 = specs.doubleAt("d4");
        double l3 = specs.doubleAt("l3");
        double l4 = specs.doubleAt("l4");
        // journal diameter
        Rect2 a1bb = dims.get(D4).arrow1.getBBox();
        Rect2 dlbb = dims.get(D4).label.getBBox();
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double lx = l3 + l4 + dlbb.w * .6;
        double ly = 0;
        boolean outside = false;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d4) {
            outside = true;
            ly = -d4 / 2 - ll;
        }
        dims.get(D4).config(new Dict("name", "d4",
                                     "value", d4,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l3 + l4,
                                                                d4 / 2),
                                     "ref2", new Vec2(l3 + l4,
                                                                -d4 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // journal length
        a1bb = dims.get(L4).arrow1.getBBox();
        dlbb = dims.get(L4).label.getBBox();
        lx = l3 + (l4 / 2);
        ly = d3 / 2 + dlbb.h * 2;
        outside = false;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l4) {
            outside = true;
            lx = l3 + l4 + dlbb.w * .6;
        }
        dims.get(L4).config(new Dict("name", "l4",
                                     "value", l4,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(l3, d3 / 2),
                                     "ref2", new Vec2(l3 + l4,
                                                                d4 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
    @Override
    public Model getModel() {
        Model model = new Model();
        double r1 = specs.doubleAt("d1") / 2;
        double r2 = specs.doubleAt("d2") / 2;
        double r3 = specs.doubleAt("d3") / 2;
        double r4 = specs.doubleAt("d4") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double l4 = specs.doubleAt("l4");
        model.add(Mesh.revolvePath(Util.objList(new Vec2(0, 0),
                                                new Vec2(0, r1),
                                                new Vec2(l1, r1),
                                                new Vec2(l1, r2),
                                                new Vec2(l2, r2),
                                                new Vec2(l2, r3),
                                                new Vec2(l3, r3),
                                                new Vec2(l3, r4),
                                                new Vec2(l3 + l4, r4),
                                                new Vec2(l3 + l4, 0))));
        return model;
    }
}
