/*
  W11V9Sketch.java
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
   A 1A1 wheel sketch.

   The only editable dimension is the diameter. A 3.75 diameter wheel has a
   bond width of about .375. Dressing the wheel reduces this width and the
   overall width of the wheel. This in turn reduces the diameter of the wheel.
   So... when the user changes the diameter, the width will be reduced, but
   within limits. The outside bond angle to the centerline is 20 degrees. With
   a bond width of .375, that give you .375 * tan(20) = .140. So a 3.75
   diameter wheel's absolute minimum diameter would be 3.75 - .14 * 2 = 3.47.
 */
@SuppressWarnings("serial")
public class W11V9Sketch extends WheelSketch {
    protected static final int D1 = 0;
    /*
      Specs for a new 3.75 dia x 1.5 length 3M wheel 
    */
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 3.77,  // od at bond (mutable)
                   "d2", 3.77,  // factory od (read only constant)
                   "d3", 2.39,  // body id (read only constant)
                   "l1", 1.518, // factory oal (read only constant)
                   "l2", .4,    // factory bond width (read only, horizontal)
                   "l3", .14,   // factory bond height (read only, vertical)
                   "l4", .3937); // body wall thick (read only)
    public W11V9Sketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W11V9Sketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W11V9Sketch(Dict d, SketchScene scene, boolean mirror)
    {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "d1"));
    }
    // copy
    static public Dict getDefaultSpecs() {
        return (Dict)DEFAULT_SPECS.clone();
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        // System.out.println("W11V9Sketch.checkGeometry()");
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double d1 = d.doubleAt("d1");
        double d2 = d.doubleAt("d2");
        double l2 = d.doubleAt("l2");
        double dy = l2 * Math.tan(Math.toRadians(20));
        return d1 <= d2          // od not too big
            && d1 > d2 - dy * 2; // od not too small
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        // System.out.println("W11V9Sketch.updateProfile() zlen = " + zlen);
        doReset();
        // setWindingRule(Path2D.WIND_EVEN_ODD);
        double od = specs.doubleAt("d1");
        double fod = specs.doubleAt("d2");
        double bid = specs.doubleAt("d3");
        double foal = specs.doubleAt("l1");
        double bw = specs.doubleAt("l2");
        double bh = specs.doubleAt("l3");
        double wallThick = specs.doubleAt("l4");
        double bidr = bid / 2;
        double odr = od / 2;
        double fodr = fod / 2;
        double tan20 = Math.tan(Math.toRadians(20));
        double tan4_5 = Math.tan(Math.toRadians(4.5));
        double yd = foal * tan20;
        double p1y = fodr - yd;
        double xdiff = (fod - od) / 2 / tan20;
        double oal = foal - xdiff;
        // 
        Vec2 p0 = new Vec2(0, holeDia / 2);
        Vec2 p1 = new Vec2(0, p1y);
        Vec2 p2 = new Vec2(oal, odr); // x is used for z length calculation
        Vec2 p3 = new Vec2(oal - bh * tan4_5, odr - bh);
        Vec2 p4 = new Vec2(p3.x - (bw - xdiff), p3.y - (bw - xdiff) * tan20);
        Vec2 p5 = new Vec2(p4.x, p4.y - .03);
        Vec2 p6 = new Vec2(wallThick, bidr);
        Vec2 p7 = new Vec2(wallThick, holeDia / 2);
        // 
        moveTo(p0.x, p0.y);
        lineTo(p1.x, p1.y);                                // 1
        lineTo(p2.x, p2.y);                                // 1
        lineTo(p3.x, p3.y);                                // 1
        lineTo(p4.x, p4.y);                                // 1
        lineTo(p5.x, p5.y);                                // 1
        lineTo(p6.x, p6.y);                                // 1
        lineTo(p7.x, p7.y);                                // 1
        closePath();
        /*
          TODO: if I add this bond line, it will not fill correctly
        */
        moveTo(p4.x, p4.y);
        lineTo(p4.x, p4.y + bh);
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
            super.updateZLenProfile(p2.x);
    }
    @Override
    protected void updateDims() {
        if (zlen != 0)
            return;
        double sx = mirror ? -1 : 1;
        Vec2[] vs = getPathVerts();
        double tipx = Math.abs(vs[2].x);
        double tipy = vs[2].y;
        // d1 (diameter, right, center or bottom)
        double d1 = specs.doubleAt("d1");
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double dlx = tipx + dlbb.w * .5 + dlg;
        double dly = 0;
        if (a1bb.h * 2 + dlbb.h + dlg > d1) {
            outside = true;
            dly = -tipy - dlbb.h * .5 - dlg - ll; // below wheel
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(dlx * sx, dly),
                                     "ref1", new Vec2(tipx * sx,
                                                                tipy),
                                     "ref2", new Vec2(tipx * sx,
                                                                -tipy),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
    }
}
