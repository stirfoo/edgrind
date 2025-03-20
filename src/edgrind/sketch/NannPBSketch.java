/*
  NannPBSketch.java
  S. Edward Dolan
  Wednesday, November  1 2023
*/

package edgrind.sketch;

//
import edgrind.Dict;
//
import edgrind.geom.*;

/**
   A Nann pullback collet sketch.

   <p>
   Coned collets have an <em>l2</em> parameter for the oal, flat collets
   only have an <em>l1</em> parameter for the oal.
   </p>
 */
@SuppressWarnings("serial")
public class NannPBSketch extends Sketch {
    static final int D1 = 0, D2 = 1;
    static final int L1 = 2;
    public NannPBSketch(Dict specs, SketchScene scene) {
        super(specs, scene);
        dims.add(new LinearDim(scene, this, "d1"));
        dims.add(new LinearDim(scene, this, "d2"));
        if (specs.hasKey("l2"))
            dims.add(new LinearDim(scene, this, "l2"));
        else
            dims.add(new LinearDim(scene, this, "l1"));
    }
    public NannPBSketch(SketchScene scene) {
        // Nann 3718E-V 1.0 bore collet
        this(new Dict("d1", 1.7616, "d2", 1., "l1", .1, "l2", .66),
             scene);
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        if (d.hasKey("l2"))
            return d.doubleAt("d1") > d.doubleAt("d2")
                && d.doubleAt("l2") > d.doubleAt("l1");
        else
            return d.doubleAt("d1") > d.doubleAt("d2");
    }
    @Override
    protected void updateProfile() {
        doReset();
        if (specs.hasKey("l2"))
            updateConedProfile();
        else
            updateFlatProfile();
    }
    private void updateFlatProfile() {
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double l1 = specs.doubleAt("l1");
        append(new Rect2(0, -d1 / 2, l1, d1));
        moveTo(0, d2 / 2);
        lineTo(l1, d2 / 2);
        moveTo(0, -d2 / 2);
        lineTo(l1, -d2 / 2);
    }
    private void updateConedProfile() {
        double d1 = specs.doubleAt("d1");
        double d2 = specs.doubleAt("d2");
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double dx = (l2 - l1) * Math.tan(Math.toRadians(32.5));
        moveTo(0, 0);
        lineTo(0, d1 / 2);
        lineTo(l1, d1 / 2);
        lineTo(l2, d1 / 2 - dx);
        lineTo(l2, 0);
        // bore line
        moveTo(0, d2 / 2);
        lineTo(l2, d2 / 2);
        mirrorH();
    }
    @Override
    protected void updateDims() {
        // d1 (od)
        double d1 = specs.doubleAt("d1");
        double ll = scene.getPixelSize(SketchDim.LEADER_LEN);
        double dlg = scene.getPixelSize(SketchDim.DIM_LABEL_GAP);
        Rect2 a1bb = dims.get(D1).arrow1.getBBox();
        Rect2 dlbb = dims.get(D1).label.getBBox();
        double lx = -dlbb.w - dlg;
        double ly = 0;
        boolean outside = false;
        if (a1bb.h * 2 + dlg * 2 > d1) {
            ly = -d1 / 2 - dlbb.h / 2 - ll;
            outside = true;
        }
        dims.get(D1).config(new Dict("name", "d1",
                                     "value", d1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(0, -d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        // d2 (bore dia)
        boolean gotL2 = specs.hasKey("l2");
        double d2 = specs.doubleAt("d2");
        double len = specs.doubleAt("l1");
        if (gotL2)
            len = specs.doubleAt("l2");
        a1bb = dims.get(D2).arrow1.getBBox();
        dlbb = dims.get(D2).label.getBBox();
        lx = len + dlbb.w / 2 + dlg;
        ly = 0;
        outside = false;
        if (a1bb.h * 2 + dlg * 2 > d2) {
            ly = -d2 / 2 - dlbb.h / 2 - ll;
            outside = true;
        }
        dims.get(D2).config(new Dict("name", "d2",
                                     "value", d2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(len, d2 / 2),
                                     "ref2", new Vec2(len, -d2 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_DIA,
                                     "force", "vertical"));
        if (!gotL2)
            updateFlatDims(d1, ll, dlg);
        else
            updateConeDims(d1, d2, len, ll, dlg);
    }
    private void updateFlatDims(double d1, double ll, double dlg) {
        // oal
        double l1 = specs.doubleAt("l1");
        Rect2 a1bb = dims.get(L1).arrow1.getBBox();
        Rect2 dlbb = dims.get(L1).label.getBBox();
        double lx = l1 / 2;
        double ly = d1 / 2 + dlbb.h / 2 + dlg;
        boolean outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l1) {
            outside = true;
            lx = l1 + dlbb.w / 2 + ll;
        }
        dims.get(L1).config(new Dict("name", "l1",
                                     "value", l1,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", new Vec2(0, d1 / 2),
                                     "ref2", new Vec2(l1, d1 / 2),
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
    private void updateConeDims(double d1, double d2, double l2, double ll,
                                double dlg) {
        // oal
        Vec2[] vs = getPathVerts();
        Rect2 a1bb = dims.get(L1).arrow1.getBBox();
        Rect2 dlbb = dims.get(L1).label.getBBox();
        double lx = l2 / 2;
        double ly = d1 / 2 + dlbb.h / 2 + dlg;
        boolean outside = false;
        if (a1bb.w * 2 + dlbb.w + dlg > l2) {
            outside = true;
            lx = l2 + dlbb.w / 2 + ll;
        }
        dims.get(L1).config(new Dict("name", "l2",
                                     "value", l2,
                                     "pos", new Vec2(lx, ly),
                                     "ref1", vs[1],
                                     "ref2", vs[3],
                                     "outside", outside,
                                     "format", SketchDim.FMT_LIN,
                                     "force", "horizontal"));
    }
}
