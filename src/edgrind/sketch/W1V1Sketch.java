/*
  W1V1Sketch.java
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
          <-l1-->
          .
          |'.      ^
          |  '.    |
          |    '.  |
          |'.   |  |
          |  '. |  |
          |    '|  |     The angle (not show) is measured from the
          |     |  |     horizontal.    
          |     |  |
          |     |  |
          |     |  d1
          |     |  |
          |     |  |
        | |     .  |
        | |   .'|  |
        v | .'  |  |
          |'    .  |
          |   .'   |
          | .'     |
          .'       v
        ^
        |
        l2 (bond height)
 */

/**
   A 1V1 wheel sketch.
 */
@SuppressWarnings("serial")
public class W1V1Sketch extends WheelSketch {
    protected static final int D1 = 0;
    protected static final int L1 = 1, L2 = 2;
    protected static final int A1 = 3;
    protected static final int R1 = 4;
    /**
       The dimension of a basic 1V1 wheel.
       <p>
       The dict string keys are as follows:
       <ul>
       <li>d1 -- outside diameter</li>
       <li>l1 -- width</li>
       <li>l2 -- bond height</li>
       <li>a1 -- angle from the centerline</li>
       <li>r1 -- corner radius</li>
       </ul>
       </p>
    */
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 6,     // outside diameter
                   "l1", .5,    // width
                   "l2", .5,    // bond height
                   "a1", 45,    // angle from the cl
                   "r1", .005); // corner radius
    /**
       Constructor.
    */
    public W1V1Sketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W1V1Sketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W1V1Sketch(Dict d, SketchScene scene, boolean mirror)
    {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new LinearDim(scene, this, "l2"));
        dims.add(new AngleDim(scene, this, "a1"));
        dims.add(new RadiusDim(scene, this, "r1"));
    }
    // return a copy
    static public Dict getDefaultSpecs() {
        return (Dict)DEFAULT_SPECS.clone();
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        // System.out.println("W1V1Sketch.checkGeometry()");
        Dict d = (Dict)this.specs.clone(); // local specs to work with
        d.putAll(specs);
        double wd = d.doubleAt("d1");
        double ww = d.doubleAt("l1");
        double bh = d.doubleAt("l2");
        double a = d.doubleAt("a1");
        double r = d.doubleAt("r1");
        double dr = wd / 2;
        double hr = holeDia / 2;
        if (r >= dr - hr)
            return false;       // bad radius
        if (bh < r)
            return false;       // bond height < radius
        if (a >= 90 || a <= 0)
            return false;       // bad angle
        double tana = Math.toRadians(90 - a);
        double ex = r + r * Math.cos(tana);
        // NOTE: epsilon fudge factor used here
        if (Math.abs(ex - ww) < 0.005)
            return false;       // bad radius + angle
        double ey = dr - r + r * Math.sin(tana);
        double dy = (ww - ex) * Math.tan(Math.toRadians(a));
        if (ey - dy < hr)
            return false;       // bad angle
        fetchAngledLine();
        return true;
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.err.println("W1V1Sketch.updateProfile:" + specs);
        doReset();
        double wd = specs.doubleAt("d1"); // diameter
        double ww = specs.doubleAt("l1"); // width
        double bh = specs.doubleAt("l2"); // bond height
        double a = specs.doubleAt("a1"); // angle
        double r = specs.doubleAt("r1"); // corner radius
        double wr = wd / 2;
        double hr = holeDia / 2;
        Line2 line2 = fetchAngledLine();
        moveTo(0, hr);
        lineTo(0, wr - r); // left tangent point of radius
        arcTo(line2.x1, line2.y1, r, wr - r, ArcDirection.CLW);
        lineTo(line2.x2, line2.y2);
        lineTo(ww, hr);
        closePath();
        // bond line
        moveTo(0, wr - bh);
        lineTo(ww, wr - bh - Math.tan(Math.toRadians(a)) * ww);
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
            super.updateZLenProfile(ww);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        // System.out.println("W1V1Sketch.updateDims()");
        double x = mirror ? -1 : 1;
        double d1 = specs.doubleAt("d1");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double a1 = specs.doubleAt("a1");
        double r1 = specs.doubleAt("r1");
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        // d1 (outside diameter, left of wheel)
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double dlx = -dlbb.w * 1.5 - dlg; // dim label x
        double dly = 0;                            // gdim label y
        if (a1bb.h * 2 + dlbb.h * 1.5 > d1) {
            outside = true;
            dly = -d1 / 2 - ll;
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(r1 * x,
                                                                d1 / 2),
                                     "ref2", new Vec2(r1 * x,
                                                                -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // l1 (wheel width)
        a1bb = dims.get(L1).arrow1.getBBox();
        dlbb = dims.get(L1).label.getBBox();
        dlx = l1 / 2;
        dly = d1 / 2 + dlbb.h * .5 + dlg;
        outside = false;
        if (a1bb.w * 2 + dlbb.w * 1.2 > l1) {
            outside = true;
            dlx = l1 + dlbb.w * .5 + ll;
        }
        double b = Math.tan(Math.toRadians(a1)) * l1;
        Line2 line2 = fetchAngledLine();
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "ref1", new Vec2(0,
                                                      d1 / 2 - r1),
                                     "ref2", new Vec2(l1 * x,
                                                      line2.y2),
                                     // d1 / 2 - b),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));

        // l2 (bond height)
        a1bb = dims.get(L2).arrow1.getBBox();
        dlbb = dims.get(L2).label.getBBox();
        dlx = -dlbb.w * .5 - dlg;
        dly = d1 / 2 - (l2 / 2); // between arrows
        outside = false;
        if (a1bb.h * 2 + dlbb.h * 1.2 > l2) {
            outside = true;
            dly = d1 / 2 - l2 - dlbb.h * 2 - ll;
        }
        dims.get(L2)
            .config(new Dict("name", "l2",
                             "value", l2,
                             "pos", new Vec2(dlx * x, dly),
                             "ref1", new Vec2(0, d1 / 2),
                             "ref2", new Vec2(0, d1 / 2 - l2),
                             "outside", outside,
                             "format", SketchDim.FMT_LIN,
                             "force", "vertical"));
        // a1
        dlx = l1 + dlbb.w;
        dly = -d1 / 2 - dlbb.h * 2;
        Line2 mirline = new Line2(line2.x1 * x, -line2.y1,
                                  line2.x2 * x, -line2.y2);
        Vec2 qv = Vec2.add(new Vec2(1 * x, 0),
                           new Vec2(mirline.p1(), mirline.p2()));
        outside = true;
        dims.get(A1)
            .config(new Dict("name", "a1",
                             "value", a1,
                             "pos", new Vec2(dlx * x, dly),
                             // invisible horizontal line thru bottom point
                             "line1", new Line2(0, -d1 / 2,
                                                        l1 * x, -d1 / 2),
                             "line2", mirline,
                             "vertex", null,
                             "quadV", qv,
                             "outside", outside,
                             "format", SketchDim.FMT_ANG));
        // r1 corner radius
        a1bb = dims.get(R1).arrow1.getBBox();
        dlbb = dims.get(R1).label.getBBox();
        dlx = -dlbb.w * 1.5 - dlg; // lbl left of...
        dly = -d1 / 2 - dlbb.h * 2 - dlg; // ...and below the wheel
        double sw = 90 + (90 - a1);
        dims.get(R1).config(new Dict("name", "r1",
                                     "value", r1,
                                     "pos", new Vec2(dlx * x, dly),
                                     "arc", new Arc2(r1 * x, // cx
                                                     -d1 / 2 + r1,  // cy
                                                     r1,            // r
                                                     x == 1 ? 270 : 0,   // sa
                                                     x == 1 ? sw : -sw), // sw
                                     "outside", true,
                                     "format", SketchDim.FMT_RAD));
    }
    /**
       <p>TODO: cache the line</p>
    */
    protected Line2 fetchAngledLine() {
        // System.out.println("W1V1Sketch.fetchAngledLine()");
        double wd = specs.doubleAt("d1");
        double ww = specs.doubleAt("l1");
        double bh = specs.doubleAt("l2");
        double a = specs.doubleAt("a1");
        double r = specs.doubleAt("r1");
        double wr = wd / 2;
        double tana = Math.toRadians(90 - a);
        double sx = r + r * Math.cos(tana);
        double sy = wr - r + r * Math.sin(tana);
        double dy = (ww - sx) * Math.tan(Math.toRadians(a));
        return new Line2(sx, sy, ww, sy - dy);
    }
}
