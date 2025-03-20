/*
  W1A1Sketch.java
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
//
import edgrind.Util;

/*
            <-l1-->
            .-----.   
            |     |  ^
            |-----|  |
            |     |  |
            |     |  |
            |     |  |
            |     |  d1
          | |     |  |
          | |     |  |
          v |     |  |
            |-----|  |
            |     |  v
            '-----'
          ^       ^
          |        `-- r1
          |
          l2 (bond height)
 */

/**
   A 1A1 wheel with corner radii sketch.
 */
@SuppressWarnings("serial")
public class W1A1Sketch extends WheelSketch {
    protected static final int D1 = 0;
    protected static final int L1 = 1, L2 = 2;
    protected static final int R1 = 3;
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 6,     // diameter
                   "l1", .5,    // width
                   "l2", .25,   // bond height
                   "r1", .01);  // corner radius
    public W1A1Sketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W1A1Sketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W1A1Sketch(Dict d, SketchScene scene, boolean mirror)
    {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new RadiusDim(scene, this, "r1"));
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
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        double r1 = d.doubleAt("r1");
        return
            // bond height not bigger than the wheel dia
            l2 < d1 / 2 &&
            // bond height bigger than the  radius
            l2 > r1 && 
            // radius not too big for wheel width
            r1 * 2 < l1 &&
            // radius not to big for wheel dia
            r1 * 2 < d1;
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.out.println("W1A1Sketch.updateProfile()");
        doReset();
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double r1 = specs.doubleAt("r1");
        moveTo(0, holeDia / 2);
        lineTo(0, d1 / 2 - r1);
        arcTo(r1, d1 / 2, r1, d1 / 2 - r1, ArcDirection.CLW);
        lineTo(l1 - r1, d1 / 2);
        arcTo(l1, d1 / 2 - r1, l1 - r1, d1 / 2 - r1, ArcDirection.CLW);
        lineTo(l1, holeDia / 2);
        closePath();
        // bond line
        moveTo(0, d1 / 2 - l2);
        lineTo(l1, d1 / 2 - l2);
        /*
        // temporary arc center points, for RadiusDim debugging
        moveTo(r1, d1 / 2 - r1);  // same start and...
        lineTo(r1, d1 / 2 - r1);  // ...end point make a dot
        moveTo(l1-r1, d1 / 2 - r1);
        lineTo(l1-r1, d1 / 2 - r1);
        */
        // 
        // mirror about the horizontal axis to create the bottom half
        Shape s = AffineTransform.getScaleInstance(1, -1)
            .createTransformedShape(this);
        append(s, false);
        if (mirror) {
            s = AffineTransform.getScaleInstance(-1, 1)
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
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // d1 (diameter, right, center or bottom)
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double dlx = l1 + dlbb.w * .5 + dlg;
        double dly = 0;
        if (a1bb.h * 2 + dlbb.h * 1.5 > d1) {
            outside = true;
            dly = -d1 / 2 - ll; // below wheel
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(l1 * x,
                                                      d1 / 2),
                                     "ref2", new Vec2(l1 * x,
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
        if (a1bb.w * 2 + dlbb.w * 1.2 > l1) {
            outside = true;
            dlx = -dlbb.w * .5 - ll; // left of wheel
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l1 * x,
                                                      d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // l2 (bond height, left or wheel, centered or above arrows)
        a1bb = dims.get(L2).arrow1.getBBox();
        dlbb = dims.get(L2).label.getBBox();
        dlx = -dlbb.w * .5 - dlg; // left of wheel
        dly = -d1 / 2 + (l2 / 2); // centered between arrows
        outside = false;
        if (a1bb.h * 2 + dlbb.h * 1.2 > l2) {
            outside = true;
            dly = -d1 / 2 + l2 + + dlbb.h * .5 + ll; // above arrows
        }
        dims.get(L2)
            .config(new Dict("name", "l2",
                             "value", l2,
                             "pos", new Vec2(dlx * x, dly),
                             "ref1", new Vec2(0, -d1 / 2),
                             "ref2", new Vec2(0, -d1 / 2 + l2),
                             "outside", outside,
                             "format", SketchDim.FMT_LIN,
                             "force", "vertical"));
        // r1 corner radius
        double r1 = specs.doubleAt("r1");
        a1bb = dims.get(R1).arrow1.getBBox();
        dlbb = dims.get(R1).label.getBBox();
        dlx = l1 + dlbb.w * 2;         // lbl right of...
        dly = -d1 / 2 - dlbb.h * 2 - dlg; // ...and below the wheel
        dims.get(R1).config(new Dict("name", "r1",
                                     "value", r1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "arc", new Arc2((l1 - r1) * x, // cx
                                                     -d1 / 2 + r1,  // cy
                                                     r1,            // r
                                                     270,           // sa
                                                     x == 1 ? 90 : -90), // sw
                                     "outside", true,
                                     "format", SketchDim.FMT_RAD));
    }
    public Model getModel() {
        Model model = new Model();
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double r1 = specs.doubleAt("r1");
        double cy = d1 / 2 - r1;
        model.add(Mesh.revolvePath(Util.objList(new Vec2(0, 0),
                                                new Vec2(0, d1 / 2 - r1),
                                                new Arc2(r1, cy, r1, 180, -90),
                                                new Vec2(l1 - r1, d1 / 2),
                                                new Arc2(l1 - r1, cy, r1, 90,
                                                         -90),
                                                new Vec2(l1, 0))));
        return model;
    }
}
