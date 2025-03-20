/*
  HPS20Sketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.Color;
// 
import java.util.List;
import java.util.Arrays;
// 
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
//
import edgrind.Dict;
//
import edgrind.geom.Vec2;
import edgrind.geom.Mesh;
import edgrind.geom.Model;

/**
   A GDS Cobra HPS20 collet chuck sketch. (Super Chuck)
 */
@SuppressWarnings("serial")
public class HPS20Sketch extends Sketch {
    static final int D1 = 0, D2 = 1, D3 = 2;
    static final int L1 = 3, L2 = 4;
    public HPS20Sketch(SketchScene scene) {
        // default dims are for an HPS20 3718E L90 400007060
        this(new Dict("d1", 5.296, "l1", .85,
                      "d2", 4.915, "l2", 1.1,
                      "d3", 4.035, "l3", 1.475,
                      "d4", 2.4, "l4", 3.3,
                      "d5", 2.125, "l5", 3.575,
                      "d6", 1.965, "l6", 3.93,
                      "d7", 1.14, "l7", 4.7895),
             scene);
    }
    public HPS20Sketch(Dict d, SketchScene scene) {
        // default dims are for an HPS20 3718E L90 400007060
        super(d, scene);
        // these will be initialized in updateDims()
        // dims.add(new LinearDim(scene, this, "d1"));
        // dims.add(new LinearDim(scene, this, "d2"));
        // dims.add(new LinearDim(scene, this, "d3"));
        // dims.add(new LinearDim(scene, this, "l1"));
        // dims.add(new LinearDim(scene, this, "l2"));
    }
    @Override
    protected boolean checkGeometry(Dict specs) {
        Dict d = (Dict)this.specs.clone();
        d.putAll(specs);
        // return d.doubleAt("d3") < d.doubleAt("d2")
        //     && d.doubleAt("d2") < d.doubleAt("d1")
        //     && d.doubleAt("l2") > d.doubleAt("l1");
        return true;
    }
    // called from Sketch.config();
    @Override
    protected void updateProfile() {
        double r1 = specs.doubleAt("d1") / 2;
        double r2 = specs.doubleAt("d2") / 2;
        double r3 = specs.doubleAt("d3") / 2;
        double r4 = specs.doubleAt("d4") / 2;
        double r5 = specs.doubleAt("d5") / 2;
        double r6 = specs.doubleAt("d6") / 2;
        double r7 = specs.doubleAt("d7") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double l4 = specs.doubleAt("l4");
        double l5 = specs.doubleAt("l5");
        double l6 = specs.doubleAt("l6");
        double l7 = specs.doubleAt("l7");
        doReset();
        moveTo(0, 0);
        lineTo(0, r1);          // v
        lineTo(l1, r1);         // h
        lineTo(l1, r2);         // v
        lineTo(l2, r2);         // h
        lineTo(l3, r3);         // d
        lineTo(l3, r4);         // v
        lineTo(l4, r4);         // h
        lineTo(l5, r5);         // d
        lineTo(l5, r6);         // v
        lineTo(l6, r6);         // h
        lineTo(l7, r7);         // d
        lineTo(l7, 0);          // v
        // mirror the top half
        append(AffineTransform.getScaleInstance(1, -1)
               .createTransformedShape(this), false);
        // some vertical lines
        moveTo(l1, r1); lineTo(l1, -r1);
        moveTo(l2, r2); lineTo(l2, -r2);
        moveTo(l3, r3); lineTo(l3, -r3);
        moveTo(l4, r4); lineTo(l4, -r4);
        moveTo(l5, r5); lineTo(l5, -r5);
        moveTo(l6, r6); lineTo(l6, -r6);
    }
    @Override
    protected void updateDims() {
    }
    public Model getModel() {
        Model model = new Model(Color.gray);
        double r1 = specs.doubleAt("d1") / 2;
        double r2 = specs.doubleAt("d2") / 2;
        double r3 = specs.doubleAt("d3") / 2;
        double r4 = specs.doubleAt("d4") / 2;
        double r5 = specs.doubleAt("d5") / 2;
        double r6 = specs.doubleAt("d6") / 2;
        double r7 = specs.doubleAt("d7") / 2;
        double l1 = specs.doubleAt("l1");
        double l2 = specs.doubleAt("l2");
        double l3 = specs.doubleAt("l3");
        double l4 = specs.doubleAt("l4");
        double l5 = specs.doubleAt("l5");
        double l6 = specs.doubleAt("l6");
        double l7 = specs.doubleAt("l7");
        List<Vec2> pts = Arrays.asList(new Vec2(0, 0),
                                       new Vec2(0, r1),
                                       new Vec2(l1, r1),
                                       new Vec2(l1, r2),
                                       new Vec2(l2, r2),
                                       new Vec2(l3, r3),
                                       new Vec2(l3, r4),
                                       new Vec2(l4, r4),
                                       new Vec2(l5, r5),
                                       new Vec2(l5, r6),
                                       new Vec2(l6, r6),
                                       new Vec2(l7, r7),
                                       new Vec2(l7, 0));
        model.add(Mesh.revolvePolyline(pts));
        return model;
    }
}
