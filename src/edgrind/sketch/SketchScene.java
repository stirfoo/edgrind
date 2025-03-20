/*
  SketchScene.java
  S. Edward Dolan
  Tuesday, September  5 2023
*/

package edgrind.sketch;

import javax.swing.JTextField;        // for...
import javax.swing.border.LineBorder; // ...dimEdit
// 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Shape;
// 
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
//
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;  // for sorting the array of sketches by z-order
//
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
//
import edgrind.Dict;
//
import edgrind.geom.*;

/**
   View one or more Sketch instances.
 */
@SuppressWarnings("serial")
public class SketchScene extends AGraphicsView {
    protected final static int PAD_PIXELS = 10; // fit margin
    protected ArrayList<Sketch> sketches = new ArrayList<Sketch>();
    protected Graphics2D g2d = null;
    protected DimEdit dimEdit;
    protected DimLabel curLabel;
    protected Origin origin;
    protected boolean firstPaint = true;
    // 
    public SketchScene() {
        super();
        origin = null;
        setLayout(null);
        addDimEdit();
    }
    public SketchScene(boolean showOrigin) {
        this();
        if (showOrigin)
            origin = new Origin(this);
    }
    /**
       Do nothing if sketch is null.
    */
    public void addSketch(Sketch sketch) {
        if (sketch != null) {
            sketches.add(sketch);
            Collections.sort(sketches); // sort by Sketch.zValue
            fitAll();
        }
    }
    /**
       Do nothing if sketch is null.
    */
    public void removeSketch(Sketch sketch) {
        if (sketch != null) {
            // will do nothing if sketch is null...
            sketches.remove(sketch);
            // ...but the check will prevent an unnecessary fitAll
            fitAll();
        }
    }
    /**
       @return the sketch at the given index, or null if not present
     */
    public Sketch getSketch(int i) {
        return sketches.get(i);
    }
    /**
       @return a list of all sketches in this scene
     */
    public ArrayList<Sketch> getSketches() {
        return sketches;
    }
    /**
       @return true if the dim edit box is not visible
    */
    public boolean canRead() {
        return !dimEdit.isVisible();
    }
    protected void addDimEdit() {
        dimEdit = new DimEdit(this);
        dimEdit.setBorder(new LineBorder(Color.green, 0));
        dimEdit.setHorizontalAlignment(JTextField.CENTER);
        Dimension d = dimEdit.getPreferredSize();
        dimEdit.setBounds(100, 100, (int)d.getWidth(), (int)d.getHeight());
        dimEdit.setVisible(true);
        dimEdit.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                        dimEdit.setVisible(false);
                        dimEdit.scene.grabFocus();
                    }
                    else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        if (!dimEdit.isOk())
                            return;
                        else {
                            dimEdit.setVisible(false);
                            curLabel.sketch
                                .config(new Dict(curLabel.getName(),
                                                 dimEdit.getValue()));
                            dimEdit.scene.grabFocus();
                            dimEdit.scene.fitAll();
                        }
                    }
                }
            });
        dimEdit.setVisible(false);
        add(dimEdit);
    }
    protected void showDimEdit() {
        final int pad = 5;
        Rect2 hbb = /*curLabel.dcbox*/ curLabel.getDCBox();
        dimEdit.setText("+123.4567");
        Dimension d = dimEdit.getPreferredSize();
        dimEdit.setLabel(curLabel);
        int x = ((int)d.getWidth() + pad - (int)hbb.w) / 2;
        int y = ((int)d.getHeight() + pad - (int)hbb.h) / 2;
        dimEdit.setBounds((int)hbb.x - x,
                          (int)hbb.y - y,
                          (int)d.getWidth() + pad,
                          (int)d.getHeight() + pad);
        dimEdit.selectAll();
        dimEdit.setVisible(true);
        dimEdit.grabFocus();
    }
    /*
      D E B U G   M E T H O D
    */
    private void renderPoint(Vec2 p, Graphics2D g2d) {
        double sz = 3.5;
        try {
            Vec2 pp = p.xform(xform);
            Rectangle2D r
                = new Rectangle2D.Double(pp.x-sz, pp.y-sz, sz*2, sz*2);
            g2d.draw(r);
            g2d.fill(r);
        }
        catch(Exception ignore) {
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // clear the scene
        g2d = (Graphics2D)g;
        // This whole iterative fit algo isn't working...
        if (firstPaint) {
            firstPaint = false;
            fitAll();           // complete...
            fitAll();           // ...and total...
            fitAll();           // ...kaka!
        }
        for (Sketch s : sketches)
            if (s != null && s.isVisible())
                s.render(g2d);
        if (origin != null)
            origin.render(g2d);
        renderMouseCoords(g2d);
    }
    @Override
    public void componentResized(ComponentEvent e) {
        curLabel = null;
        dimEdit.setVisible(false);
        fitAll();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        for (Sketch sketch : sketches)
            if (sketch != null && !sketch.isReadOnly()) {
                for (SketchDim d : sketch.getDims()) {
                    if (d.isReadOnly())
                        continue;
                    d.label.setHovered(false);
                    // TODO: label get dc bbox method
                    // if (d.label.dcbox.toRectangle2D().contains(e.getPoint()))
                    if (d.label.getDCBox().toRectangle2D()
                        .contains(e.getPoint()))
                        d.label.setHovered(true);
                }
            }
        repaint();
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        for (Sketch sketch : sketches)
            if (sketch != null && !sketch.isReadOnly()) {
                for (SketchDim d : sketch.getDims()) {
                    if (d.isReadOnly())
                        continue;
                    // if (d.label.dcbox.toRectangle2D().contains(e.getPoint())) {
                    if (d.label.getDCBox().toRectangle2D()
                        .contains(e.getPoint())) {
                        curLabel = d.label;
                        showDimEdit();
                        return;
                    }
                }
            }
        dimEdit.setVisible(false);
        grabFocus();
    }
    void fitInView(Rect2 r) {
        if (r.isEmpty() || getWidth() == 0 || getHeight() == 0)
            return;
        xform = AffineTransform.getScaleInstance(1, -1); // invert the y
        double xratio = (getWidth() - PAD_PIXELS * 2) / r.w;
        double yratio = (getHeight() - PAD_PIXELS * 2) / r.h;
        xratio = yratio = Math.min(xratio, yratio);
        xform.scale(xratio, yratio);
        centerOn(r.center());
        repaint();
    }
    void centerOn(Vec2 p) {
        try {
            // uSER sPACE cENTER pOINT
            Vec2 uscp = new Vec2(getWidth() / 2, getHeight() / 2)
                .xform(xform.createInverse());
            xform.translate(uscp.x - p.x, uscp.y - p.y);
        }
        catch (Exception ignore) {
            System.err.println("oops! SketchScene.centerOn()");
        }
    }
    public void fitAll() {
        if (sketches.isEmpty())
            return;
        final int maxIters = 20;
        final double maxChange = 0.0000001;
        double ps = 0, pps = 0;
        int iters = 1;
        while (true) {
            Rect2 r = new Rect2();
            for (Sketch s : sketches)
                r.add(s.getBBox());
            if (origin != null)
                r.add(origin.getBBox());
            fitInView(r);
            pps = getPixelSize(1);
            for (Sketch s : sketches)
                s.config();
            if (origin != null)
                origin.config();
            if (iters == maxIters || Math.abs(ps - pps) < maxChange)
                break;
            ps = pps;
            ++iters;
        }
    }
}

/**
  A graphical representation of the X/Y zero point in a sketch.

  The shape is drawn in pixel coordinates so it will always be the same size
  on the screen.

  @param R the circle diameter, in pixels
  @param LINE_EXT the dist the crosshairs extend beyond the circle in pixels
  @param color the fill and line color
  @param scene the parent SketchScene that will render this Origin
  @param ucbox bounding box in user coordinates (uc)
  @param circle the graphical circle and crosshairs, not filled
  @param wedges the two opposed pie slices, filled
 */
@SuppressWarnings("serial")
class Origin {
    static final int R = 15;                   // circle radius in pixels
    static final int LINE_EXT = (int)(R * .75); // line ext in pixels
    Color color = Color.white;
    SketchScene scene;
    Rect2 ucbox = new Rect2();
    Path2 circle = new Path2(); // rendered as an outline
    Path2 wedges = new Path2(); // rendered filled
    // 
    Origin(SketchScene scene) {
        this.scene = scene;
    }
    void config() {
        AffineTransform m = scene.xform;
        Point2D p = m.transform(new Point2D.Double(0., 0.), null);
        // center of origin in pixels
        double x = p.getX(), y = p.getY();
        circle.doReset();
        circle.append(new Ellipse2D.Double(x - R, y - R, R*2, R*2), false);
        circle.moveTo(x - R - LINE_EXT, y);
        circle.lineTo(x + R + LINE_EXT, y);
        circle.moveTo(x, y - R - LINE_EXT);
        circle.lineTo(x, y + R + LINE_EXT);
        wedges.doReset();
        Arc2D a = new Arc2D.Double(Arc2D.PIE);
        a.setArcByCenter(x, y, R, 0, 90, Arc2D.PIE);
        wedges.append(a, false);
        a.setArcByCenter(x, y, R, 180, 90, Arc2D.PIE);
        wedges.append(a, false);
        try {
            Shape s = scene.xform.createInverse()
                .createTransformedShape(circle.getBounds2D());
            ucbox = new Rect2(s.getBounds2D());
        }
        catch (Exception ignore) {
        }
    }
    void render(Graphics2D g2d) {
        g2d.setPaint(color);
        g2d.draw(circle);
        g2d.fill(wedges);
    }
    Rect2 getBBox() {
        return ucbox;
    }
}
