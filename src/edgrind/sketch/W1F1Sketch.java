/*
  W1F1Sketch.java
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

/**
   A 1F1 wheel sketch.
 */
@SuppressWarnings("serial")
public class W1F1Sketch extends WheelSketch {
    protected static final int D1 = 0; // wheel diameter dim index
    protected static final int L1 = 1; // bond height dim index
    protected static final int R1 = 2; // radius dim index
    /**
       The dimension of a 1F1 wheel.
       <p>
       The dict string keys are as follows:
       <ul>
       <li>d1 -- diameter</li>
       <li>l1 -- bond height</li>
       <li>r1 -- radius</li>
       </ul>
       </p>
    */
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 6,     // outside diameter
                   "r1", .25,   // radius
                   "l2", .3);   // bond height
    /**
       Constructor.
    */
    public W1F1Sketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W1F1Sketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W1F1Sketch(Dict d, SketchScene scene, boolean mirror)
    {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new RadiusDim(scene, this, "r1"));
    }
    // return a copy
    static public Dict getDefaultSpecs() {
        return (Dict)DEFAULT_SPECS.clone();
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        // System.out.println("W1F1Sketch.checkGeometry()");
        Dict d = (Dict)this.specs.clone(); // local specs to work with
        d.putAll(specs);
        double wd = d.doubleAt("d1");
        double bh = d.doubleAt("l1");
        double r = d.doubleAt("r1");
        double wr = wd / 2;
        double hr = holeDia / 2;
        return
            wr > hr &&          // dia > hole dia
            bh > r;             // bond height > radius
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.out.println("W1F1Sketch.updateProfile()");
        doReset();
        double wd = specs.doubleAt("d1"); // diameter
        double bh = specs.doubleAt("l1"); // width
        double r = specs.doubleAt("r1"); // bond height
        double wr = wd / 2;
        moveTo(0, holeDia / 2);
        lineTo(0, wr - r); // left tangent point of radius
        arcTo(r * 2, wr - r, r, wr - r, ArcDirection.CLW);
        lineTo(r * 2, holeDia / 2);
        closePath();
        // bond line
        moveTo(0, wr - bh);
        lineTo(r * 2, wr - bh);
        // mirror about the horizontal axis to create the bottom half
        Shape s = AffineTransform.getScaleInstance(1, -1)
            .createTransformedShape(this);
        append(s, false);
        if (mirror) {
            // mirror about the vertical axis and replace
            s = AffineTransform.getScaleInstance(-1, 1)
                .createTransformedShape(this);
            doReset();
            append(s, false);
        }
        if (zlen > 0)
            super.updateZLenProfile(r * 2);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        // System.out.println("W1F1Sketch.updateDims()");
        double x = mirror ? -1 : 1;
        double wd = specs.doubleAt("d1");
        double bh = specs.doubleAt("l1");
        double r = specs.doubleAt("r1");
        double ww = r * 2;
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // wd (wheel diameter, right of wheel)
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double dlx = ww + dlbb.w * .5 + dlg;
        double dly = 0;
        if (a1bb.h * 2 + dlbb.h * 1.5 > wd) {
            outside = true;
            dly = -wd / 2 - dlbb.h / 2 - ll;
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", wd,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(r * x, wd / 2),
                                     "ref2", new Vec2(r * x,
                                                                -wd / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // bh (bond height)
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        dlx = -dlbb.w * .5 - dlg;
        dly = -wd / 2 + (bh / 2); // between arrows
        outside = false;
        if (a1bb.h * 2 + dlbb.h + dlg > bh) {
            outside = true;
            dly = -wd / 2 - dlbb.h * .5 - ll;
        }
        dims.get(L1)
            .config(new Dict("name", "l1",
                             "value", bh,
                             "pos", new Vec2(dlx * x, dly),
                             "ref1", new Vec2(0, -wd / 2 + bh),
                             "ref2", new Vec2(0, -wd / 2),
                             "outside", outside,
                             "format", SketchDim.FMT_LIN,
                             "force", "vertical"));
        // r1 corner radius
        a1bb = dims.get(R1).arrow1.getBBox();
        dlbb = dims.get(R1).label.getBBox();
        dlx = dlbb.w + ww + dlg; // lbl right of...
        dly = -wd / 2 - dlbb.h * 2 - dlg; // ...and below the wheel
        dims.get(R1).config(new Dict("name", "r1",
                                     "value", r,
                                     "pos", new Vec2(dlx * x, dly),
                                     "arc", new Arc2(r * x,              // cx
                                                     -wd / 2 + r,        // cy
                                                     r,                  // r
                                                     270,                // sa
                                                     x == 1 ? 90 : -90), // sw
                                     "outside", true,
                                     "format", SketchDim.FMT_RAD));
    }
}
