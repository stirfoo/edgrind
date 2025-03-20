/*
  DimLabel.java
  S. Edward Dolan
  Monday, September 11 2023
*/

package edgrind.sketch;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
//
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Color;
// 
import edgrind.Dict;
// 
import java.awt.font.FontRenderContext;
//
import edgrind.geom.*;

/**
  The text of a dimension.
 */
class DimLabel {
    static final int PADDING = 3; // extra pixels around the text bbox
    /**
       The specs dict.
       <dl>
       <dt>name</dt><dd>the name of the associated SketchDim</dd>
       <dt>pos</dt><dd>the label's center coordinates in sketch space</dd>
       <dt>text</dt><dd>the label's text</dd>
       <dt>vertical</dt><dd>anything</dd>
       </dl>
       
       <p>The vertical key is only checked for presence. It's value may be
       any Object or null.</p>
    */
    Dict specs;
    Vec2 deviceCP = new Vec2();
    Vec2 textPt = new Vec2();   // bottom left corner of text in device coords
    Rect2 dcbox = new Rect2();  // bbox in pixels w/padding
    Rect2 ucbox = new Rect2();  // bbox in user coords w/p
    SketchScene scene;
    Sketch sketch;
    boolean hovered;
    DimLabel(SketchScene scene, Sketch sketch) {
        this(new Dict("pos", new Vec2(), // in user space
                      "text", "1.0"),
             scene, sketch);
    }
    DimLabel(Dict d, SketchScene scene, Sketch sketch) {
        this.scene = scene;
        this.sketch = sketch;
        config(specs = (Dict)d.clone());
    }
    Rect2 getBBox() {
        return ucbox;
    }
    Rect2 getDCBox() {
        if (specs.hasKey("vertical"))
            return dcbox.getBBox(AffineTransform
                                 .getRotateInstance(Math.toRadians(-90),
                                                    deviceCP.x,
                                                    deviceCP.y));
        return dcbox;
    }
    void setHovered(boolean b) {
        hovered = b;
    }
    public String getName() {
        return specs.stringAt("name");
    }
    // remove prefix and suffix characters returning just the numeric value
    public String getText() {
        String s = specs.stringAt("text");
        int i = 0, j = s.length();
        if (!s.isEmpty()) {
            char c = s.charAt(0);
            if (c == 'Ø' || c == 'R')
                i = 1;
            if (s.charAt(j-1) == '°')
                --j;
        }
        return s.substring(i, j);
    }
    void config(Dict d) {
        if (d != null)
            specs.putAll(d);
        if (scene.g2d == null)
            return;
        // center of label bounding box in device coordinates
        deviceCP = specs.vecAt("pos").xform(scene.xform);
        FontRenderContext c = scene.g2d.getFontRenderContext();
        // pixel bounding rect without padding or rotation
        Rect2 r = new Rect2(scene.g2d.getFont()
                            .getStringBounds(specs.stringAt("text"),
                                             c));
        double xc = deviceCP.x;
        double yc = deviceCP.y;
        // pixel bbox with padding
        dcbox = new Rect2(xc - r.w / 2 - PADDING, yc - r.h / 2 - PADDING,
                          r.w + PADDING * 2, r.h + PADDING * 2);
        textPt = new Vec2(xc - r.w / 2, yc + r.h / 2);
        try {
            Shape s = dcbox.xformShape(scene.xform.createInverse());
            // bbox in user space
            this.ucbox.set(s.getBounds2D());
        }
        catch (Exception e) {
            System.out.println("DimLabel.config(Dict d) {...oops!...}");
        }
        setHovered(false);
    }
    void render(Graphics2D g) {
        if (specs.hasKey("vertical")) {
            AffineTransform t = g.getTransform();
            g.setTransform(AffineTransform.getRotateInstance(Math
                                                             .toRadians(-90),
                                                             deviceCP.x,
                                                             deviceCP.y));
            renderLabel(g);
            g.setTransform(t);
        }
        else
            renderLabel(g);
    }
    private void renderLabel(Graphics2D g) {
        // clear to the background to erase anything under it
        g.clearRect((int)dcbox.x, (int)dcbox.y, (int)dcbox.w, (int)dcbox.h);
        if (hovered) {
            g.setPaint(Color.green);
            g.draw(dcbox.toRectangle2D());
        }
        g.setPaint(Color.green);
        g.drawString(specs.stringAt("text"), (int)textPt.x, (int)textPt.y);
    }
}
