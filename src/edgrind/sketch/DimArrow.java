/*
  DimArrow.java
  S. Edward Dolan
  Sunday, September 10 2023
*/

package edgrind.sketch;

import java.awt.geom.AffineTransform;
//
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
//
import edgrind.Dict;
// 
import edgrind.geom.*;

/**
   A dimension arrow head.

   The path's coordinates are in device coords so the arrow does not scale and
   will always be rendered the same-ish size on a given device.

   The specs Dict is as follows:
   <dl>
   <dt>"pos"</dt>
   <dd>a Vec2, the arrow tip's position in sketch coords</dd>
   <dt>"dir"</dt>
   <dd>a Vec2, the direction the arrow is pointing</dd>
   </dl>
 */
@SuppressWarnings("serial")
public class DimArrow extends Path2 {
    protected final static int LENGTH = 14; // pixel size
    protected final static int WIDTH = 6;   // of the arrow
    protected final static Color color = Color.green;
    // pixel coords with the arrow pointing @ 3 o'clock
    final static Vec2 P2 = new Vec2(LENGTH, WIDTH / 2.0);
    final static Vec2 P3 = new Vec2(LENGTH, -WIDTH / 2.0);
    final static BasicStroke stroke
        = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    Rect2 ucbox = new Rect2(); // user (sketch) coords
    Dict specs;
    SketchScene scene;
    DimArrow(SketchScene scene) {
        this(new Dict("pos", new Vec2(0, 0),
                      "dir", new Vec2(1, 0)),
             scene);
    }
    DimArrow(Dict specs, SketchScene scene) {
        this.specs = (Dict)specs.clone();
        this.scene = scene;
        config();
    }
    /**
       Get the bounding box in user coords. Use getBounds2D() for the pixel
       bbox;
    */
    Rect2 getBBox() {
        return ucbox;
    }
    void config() {
        config(null);
    }
    void config(Dict d) {
        if (d != null)
            specs.putAll(d);
        AffineTransform m = scene.xform;
        // rotated about the arrow tip which is at local (0, 0)
        AffineTransform rotm = AffineTransform
            .getRotateInstance(specs.vecAt("dir").rangle());
        // p1 in device coords
        Vec2 p1 = specs.vecAt("pos").xform(m);
        // p2 and p3 are relative to (0, 0) in device coords
        Vec2 p2 = P2.xform(rotm);
        Vec2 p3 = P3.xform(rotm);
        doReset();
        // again, these are pixel coordinates
        moveTo(p1.x, p1.y);
        lineTo(-p2.x + p1.x, p2.y + p1.y);
        lineTo(-p3.x + p1.x, p3.y + p1.y);
        closePath();
        try {
            Shape s = scene.xform.createInverse()
                .createTransformedShape(getBounds2D());
            ucbox = new Rect2(s.getBounds2D());
        }
        catch (Exception ignore) {
        }
    }
    void render(Graphics2D g) {
        g.setStroke(stroke);
        g.setPaint(color);
        g.fill(this);
        g.draw(this);
        // render the bbox
        // g.setPaint(Color.red);
        // g.draw(getBounds2D());
    }
}
