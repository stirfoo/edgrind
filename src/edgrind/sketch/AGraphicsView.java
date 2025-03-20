/*
  AGraphicsView.java
  S. Edward Dolan
  Tuesday, September  5 2023
*/

package edgrind.sketch;

import javax.swing.JComponent;
// 
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
// 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.RenderingHints;
//
import java.awt.geom.AffineTransform;
//
import edgrind.geom.*;
//
import edgrind.Config;

/**
  Abstract base class for a 2d Sketch viewer.
 */
@SuppressWarnings("serial")
public abstract class AGraphicsView
    extends JComponent implements MouseListener, MouseMotionListener,
                                  ComponentListener {
    static final int PREFERRED_WIDTH = 500;
    static final int PREFERRED_HEIGHT = 500;
    static Rect2 unitRect = new Rect2(0, 0, 1, 1);
    RenderingHints rhints
        = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
    protected Color bgColor = Config.getBackgroundColor();
    // protected Color bgColor = new Color(0x2d, 0x35, 0x61); // deep purpleishly
    protected Color fgColor = Color.WHITE;
    protected Vec2 ucMousePos = new Vec2();
    protected AffineTransform xform;
    AGraphicsView() {
        setOpaque(true);        // hrm...
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        xform = new AffineTransform();
    }
    public Color getBGColor() {
        return bgColor;
    }
    public Color getFGColor() {
        return fgColor;
    }
    public double getPixelSize(int n) {
        try {
            Shape s = unitRect.xformShape(xform.createInverse());
            return s.getBounds2D().getHeight() * n;
        }
        catch (Exception ignore) {
            return 0;
        }
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }
    /**
       The base class clears the background and sets some rendering hints.
    */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // System.out.println("AGraphicsView.paint()");
        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(fgColor);
        g2d.setBackground(Config.getBackgroundColor());
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHints(rhints);
    }
    public void renderMouseCoords(Graphics2D g) {
        g.setPaint(Color.green);
        g.drawString(String.format("X%f, Y%f", ucMousePos.x, ucMousePos.y),
                     5, getHeight() - 5);
    }
    // MouseListener
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    // MouseMotionListener
    @Override public void mouseMoved(MouseEvent e) {
        try {
            ucMousePos = new Vec2(xform.createInverse()
                                  .transform(e.getPoint(), null));
        }
        catch (Exception ignore) {
        }
    }
    @Override public void mouseDragged(MouseEvent e) {}
    // ComponentListener
    @Override public void componentHidden(ComponentEvent e) {}
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
}
