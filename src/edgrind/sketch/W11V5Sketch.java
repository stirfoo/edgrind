/*
  W11V5Sketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.Shape;
// 
import java.awt.geom.AffineTransform;
// 
import java.util.List;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/**
   An 11V5 wheel sketch.

   <p>
   These wheels are pretty much fixed as far as basic size. The only
   dimensions the user can change are the overall length and bond angle. The
   initial bond width and height are fixed by the manufacturer. When the oal
   is modified the bond width changes. When the bond angle is modified, the
   bond line on the sketch moves accordingly.
   </p>

   <p>
   For instance, a 3" id wheel has a 3.5" od and is 1.375 in length. The other
   dimensions dependent on the id and oal are fixed and must be included in
   the stored dict. A system wheel named #11V5_3.0ID will define these other
   dimensions.
   </p>

   <p>
   Another system wheel named #11V5_3.5ID will define the other dimensions
   dependent on the 3.5" id and 1.375 oal.
   </p>

   <p>
   The checkGeometry() method will ensure the editable dimension stay within
   bounds.
   </p>
 */
@SuppressWarnings("serial")
public class W11V5Sketch extends WheelSketch {
    // the only 2 user-mutable dimensions
    protected static final int L1 = 0; // oal dimension index
    protected static final int A1 = 1; // bond angle dimension index
    /**
       Define the basic dimensions for a 3.0 ID x 1.375 OAL wheel.
    */
    protected static final Dict DEFAULT_SPECS
        = new Dict("d1", 3.5,   // od (read only constant)
                   "d2", 3.,    // id (read only constant)
                   "d3", 2.38,  // body id (read only constant)
                   "l1", 1.375, // oal (mutable)
                   "l2", 1.375, // initial factory oal (read only constant)
                   "l3", .197,  // initial bond width (read only variable)
                   "l4", .394,  // body wall thick (read only constant)
                   "a1", 25,    // bond angle to the vertical axis (mutable)
                   "a2", 15);  // body angle to the xaxis (read only constant)
    public W11V5Sketch(SketchScene scene, boolean mirror) {
        this(DEFAULT_SPECS, scene, mirror);
    }
    public W11V5Sketch(Dict d, SketchScene scene) {
        this(d, scene, false);
    }
    public W11V5Sketch(Dict d, SketchScene scene, boolean mirror) {
        super(d, scene, mirror);
        // these will be initialized in updateDims()
        dims.add(new LinearDim(scene, this, "l1"));
        dims.add(new AngleDim(scene, this, "a1"));
    }
    // copy
    static public Dict getDefaultSpecs() {
        return (Dict)DEFAULT_SPECS.clone();
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        double l1 = d.doubleAt("l1");
        double l2 = d.doubleAt("l2");
        double l3 = d.doubleAt("l3");
        double a1 = d.doubleAt("a1");
        return a1 > 0
            && a1 <= 45          // where's the limit to the bond angle?
            && l1 <= l2
            && l1 > l2 - l3;
    }
    @Override
    protected void updateProfile() {
        // System.out.println("W11V5Sketch.updateProfile() zlen = " + zlen);
        doReset();
        double od = specs.doubleAt("d1"); // od
        double id = specs.doubleAt("d2"); // id
        double bid = specs.doubleAt("d3"); // body id
        double oal = specs.doubleAt("l1"); // mutable oal
        double foal = specs.doubleAt("l2"); // initial fixed oal
        double fbw = specs.doubleAt("l3");  // initial fixed bond width
        double bwt = specs.doubleAt("l4");  // fixed body wall thickness
        double a1 = specs.doubleAt("a1"); // bond angle to vertical axis
        double a2 = specs.doubleAt("a2"); // body angle to horizontal axis
        double odr = od / 2;
        double idr = id / 2;
        double bidr = bid / 2;
        double hr = holeDia / 2;
        double bw = fbw - (foal - oal); // real bond width
        double bh = odr - idr;  // fixed bond height (along y axis)
        double bond_dx = bh * Math.tan(Math.toRadians(a1));
        double body_dx = oal - bond_dx - bw;
        double body_dy = body_dx * Math.tan(Math.toRadians(a2));
        moveTo(0, hr);
        lineTo(0, odr - body_dy);
        lineTo(body_dx, odr);
        lineTo(body_dx + bw, odr);
        lineTo(oal, idr);
        lineTo(oal - bw, idr);
        lineTo(bwt, bidr);
        lineTo(bwt, hr);
        closePath();
        // bond line
        moveTo(oal - bw, idr);
        lineTo(oal - bw - bond_dx, odr);
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
            super.updateZLenProfile(oal);
    }
    // called from Sketch.config()
    @Override
    protected void updateDims() {
        // System.out.println("W11V5Sketch.updateDims() zlen = " + zlen);
        if (zlen != 0)
            return;
        double sx = mirror ? -1 : 1;
        Vec2[] vs = getPathVerts();
        double oal = Math.abs(vs[4].x);
        double r = vs[2].y;
        // oal dimension
        boolean outside = false;
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        Rect2 a1bb = dims.get(L1).arrow1.getBBox();
        Rect2 dlbb = dims.get(L1).label.getBBox();
        double dlx = oal / 2;
        double dly = r + dlbb.h / 2 + dlg;
        if (a1bb.w * 2 + dlbb.w + dlg * 2 > oal) {
            outside = true;
            dlx = -dlbb.w / 2 - ll;
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", oal,
                                     "pos", new Vec2(dlx * sx, dly),
                                     "ref1", new Vec2(0, vs[1].y),
                                     "ref2", new Vec2(oal * sx,
                                                                vs[4].y),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
        // bond angle dimension
        Vec2 p1 = vs[14], p2 = vs[15];
        dlx = oal + dlbb.w + dlg;
        dly = p2.y;
        outside = true;
        dims.get(A1)
            .config(new Dict("name", "a1",
                             "value", specs.doubleAt("a1"),
                             "pos", new Vec2(dlx * sx, dly),
                             "line1", new Line2(oal * sx, p2.y,
                                                oal * sx, p1.y),
                             "line2", new Line2(p1.x, p1.y, p2.x, p2.y),
                             "vertex", null,
                             "quadV", new Vec2(1 * sx, 1),
                             "outside", outside,
                             "format", SketchDim.FMT_ANG));
        
    }
}
