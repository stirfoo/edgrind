/*
  SketchDim.java
  S. Edward Dolan
  Saturday, September  9 2023
*/

package edgrind.sketch;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
// 
import java.awt.geom.Rectangle2D;
// 
import java.awt.event.MouseEvent;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/**
  A sketch dimension.

  This is the parent class for all dimensions within a sketch.

  The class supplies an empty Dict instance for dimension configuration, a
  single DimLabel to show the value of the label, two DimArrow instances for
  possible subclass use, and default dimension metrics like gap size and
  leader length. Some format strings are provided for use with String.format()
  to display the label with for instance, a diameter or degree symbol.

  The default line color is yellow and thickness is thinner than the default
  Sketch color and line thickness.
 */
@SuppressWarnings("serial")
public abstract class SketchDim extends Path2 {
    // The following 4 values are in pixels
    /** outside leader length */
    static final int LEADER_LEN = 30;
    /** pixels past the arrow tip */
    static final int EXT_LINE_EXT = 10;
    /** pixels from the extension line end point to the reference */
    static final int EXT_LINE_GAP = 12;
    /** horizontal line length for outside radius/diameter dimensions */
    static final int JOG_LINE_LEN = 30;
    /** pixel padding around label border */
    static final int DIM_LABEL_GAP = 10;
    // 
    static final double GAP_ANGLE = 1.5;   // rotated away from ref (degrees)
    static final double EXT_ANGLE = 0.5;   // rotated passed arrow  (degrees)
    // 
    public static final String FMT_LIN = "%.4f";  //  1.2345 
    public static final String FMT_ANG = "%.2f°"; //  1.23° 
    public static final String FMT_RAD = "R%.4f"; // R1.2345 
    public static final String FMT_DIA = "Ø%.4f"; // Ø1.2345
    // less bold than Sketch geometry
    final static BasicStroke stroke
        = new BasicStroke(1.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    /** Color for all dimension lines */
    protected Color color = new Color(255, 255, 0);
    // 
    protected Dict specs;
    protected DimLabel label;
    protected SketchScene scene;
    protected Sketch sketch;
    protected DimArrow arrow1, arrow2;
    protected boolean readOnly; // selective dimension mutability
    protected boolean visible = true;
    // 
    SketchDim(SketchScene scene, Sketch sketch) {
        this(scene, sketch, false);
    }
    SketchDim(SketchScene scene, Sketch sketch, boolean readOnly) {
        super();
        this.scene = scene;
        this.sketch = sketch;
        this.readOnly = readOnly;
        specs = new Dict();
        label = new DimLabel(scene, sketch);
        arrow1 = new DimArrow(scene);
        arrow2 = new DimArrow(scene);
    }
    public boolean isReadOnly() {
        return readOnly;
    }
    public void setReadOnly(boolean b) {
        readOnly = b;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean b) {
        visible = b;
    }
    /**
       Merge d with this SketchDim's current specs dict.

       This dim label position and text are configured as well.
    */
    void config(Dict d) {
        if (d != null)
            specs.putAll(d);
        if (specs.hasKey("vertical"))
            label.config(new Dict("name", specs.stringAt("name"),
                                  "pos", specs.vecAt("pos"),
                                  "text", String.format(specs
                                                        .stringAt("format"),
                                                        specs
                                                        .doubleAt("value")),
                                  "vertical", true));
        else
            label.config(new Dict("name", specs.stringAt("name"),
                                  "pos", specs.vecAt("pos"),
                                  "text", String.format(specs
                                                        .stringAt("format"),
                                                        specs
                                                        .doubleAt("value"))));
    }
    void render(Graphics2D g) {
        if (!visible)
            return;
        g.setStroke(stroke);
        g.setPaint(color);
        // dim lines, arcs, leaders, extension lines, etc
        g.draw(scene.xform.createTransformedShape(this));
        if (arrow1 != null)
            arrow1.render(g);
        if (arrow2 != null)
            arrow2.render(g);
        label.render(g);
    }
    Rect2 getBBox() {
        Rect2 r = new Rect2(getBounds2D());
        r.add(label.getBBox());
        return r;
    }
    /**
       Add two lines to thie Path2D.

       One from p1 to ap1, the other from p2 to ap2, with applied gap and
       extension values

       
       @param p1 first reference point
       @param p2 second reference point
       @param ap1 first arrow tip point
       @param ap2 second arrow tip point
    */
    void addExtensionLines(Vec2 p1, Vec2 p2, Vec2 ap1, Vec2 ap2) {
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        double gap = scene.getPixelSize(EXT_LINE_GAP);
        Vec2 l1v = Vec2.sub(ap1, p1);
        Vec2 l1nv = Vec2.norm(l1v);
        Vec2 gp = Vec2.mul(l1nv, gap);
        Vec2 ep = Vec2.mul(l1nv, ext);
        if (l1v.mag() > gap) {
            moveTo(p1.x + gp.x, p1.y + gp.y);
            lineTo(ap1.x + ep.x, ap1.y + ep.y);
        }
        l1v = Vec2.sub(ap2, p2);
        l1nv = Vec2.norm(l1v);
        gp = Vec2.mul(l1nv, gap);
        ep = Vec2.mul(l1nv, ext);
        if (l1v.mag() > gap) {
            moveTo(p2.x + gp.x, p2.y + gp.y);
            lineTo(ap2.x + ep.x, ap2.y + ep.y);
        }
    }
}
