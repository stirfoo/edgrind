/*
  SimView
  S. Edward Dolan
  Monday, November 13 2023
*/

package edgrind;

import java.util.List;
import java.util.ArrayList;
// 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
// 
import java.awt.geom.Line2D;
// 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//
import java.awt.image.BufferedImage;
// 
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
//
import edgrind.geom.*;
//
import edgrind.error.ZeroError;
//
import edgrind.Config;

enum RenderMode {
    WIRE,
    FLAT,
    SMOOTH
};

@SuppressWarnings("serial")
public class SimView extends JPanel implements MouseMotionListener,
                                               MouseListener,
                                               ComponentListener,
                                               MouseWheelListener {
    static final double isoAngle = Math.atan2(1., Math.sqrt(2));
    MainFrame mainFrame;
    RenderMode renderMode = RenderMode.SMOOTH;
    // 
    double rotFactor = 0.5;
    Vec2 mousePos = new Vec2();
    Mat4 mvm = new Mat4();      // model view matrix (rot, trans, etc.)
    Mat4 pjm = new Mat4();      // projection matrix (ortho)
    Mat4 nsm = new Mat4();      // map NDC to screen coords
    Vec4 viewPort = new Vec4();
    // ratio of world height to window height
    double q = 0;
    // window vertex z depth based on the value of q
    double zd = 0;
    SimPopupMenu popupMenu;
    int downButton = 0;
    BufferedImage colorBuf;
    Graphics2D g2i;
    DepthBuffer depthBuf;
    double aspect = 1;
    Vec3 rotC = new Vec3();
    Vec2 wrdC = new Vec2();
    double wrdW = 10, wrdH = wrdW;
    double near = 1000, far = -1000;
    // 
    RenderingHints rhints
        = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
    // -----------------------------------------------------------------
    // Models
    static final int SP1_ADAPT_IDX = 0;
    static final int SP2_ADAPT_IDX = 1;
    static final int SP1_WHEEL1_IDX = 2;
    static final int SP1_WHEEL2_IDX = 3;
    static final int SP1_WHEEL3_IDX = 4;
    static final int SP2_WHEEL1_IDX = 5;
    static final int SP2_WHEEL2_IDX = 6;
    static final int SP2_WHEEL3_IDX = 7;
    static final int CHUCK_IDX = 8;
    static final int COLLET_IDX = 9;
    static final int PART_IDX = 10;
    Model[] models = new Model[11];
    Model currentModel;
    // 
    protected final static BasicStroke stroke
        = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public SimView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(this);
        setComponentPopupMenu(popupMenu = new SimPopupMenu(this));
    }
    protected void setRenderMode(RenderMode mode) {
        renderMode = mode;
        repaint();
    }
    protected void rotateScene(Vec3 axis, double angle) {
        mvm = mvm.mul(Mat4.translate(Vec3.neg(rotC)))
            .mul(Mat4.axisAngle(axis, Math.toRadians(angle)))
            .mul(Mat4.translate(rotC));
    }
    protected double pixelSize() {
        return 2. / (pjm.a[0][0] * getWidth());
    }
    protected void pan(double dx, double dy) {
        double ps = pixelSize();
        wrdC.sub(ps * dx, ps * -dy);
        ortho();
    }
    public void fitAll() {
        AABBox bbox = new AABBox();
        for (Model m : models)
            if (m != null)
                bbox.add(m.getBBox(mvm));
        if (bbox.isValid()) {
            rotC = bbox.center();
            fit(bbox.leftTop(), bbox.rightBottom());
        }
    }
    protected void fit(Vec2 p1, Vec2 p2) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        double w = Math.abs(x2 - x1);
        double h = Math.abs(y2 - y1);
        if (w == 0 || h == 0)
            return;
        wrdC.set((x1 + x2) * .5, (y1 + y2) * .5);
        if (w / h >= aspect)
            wrdH = w / aspect;
        else
            wrdH = h;
        wrdH *= 1.02;            // add some padding
        ortho();
        repaint();
    }
    protected void ortho() {
        wrdW = wrdH * aspect;
        pjm = Mat4.ortho(wrdC.x - wrdW * .5, // left
                         wrdC.x + wrdW * .5, // right
                         wrdC.y - wrdH * .5, // bottom
                         wrdC.y + wrdH * .5, // top
                         near, far);
        // 
        q = viewPort.w / wrdH;
        zd = (near - far) * q;
    }
    @Override
    public void componentResized(ComponentEvent e) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0)
            return;
        //
        viewPort.set(0, 0, w, h);
        nsm = Mat4.ndcToScreen(viewPort);
        //
        aspect = (double)w / h;
        ortho();
        // 
        depthBuf = new DepthBuffer(w, h);
        colorBuf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g2i = colorBuf.createGraphics();
        // g2i.setRenderingHints(rhints);
        repaint();
    }
    void renderWire(Mat4 m, Graphics2D g2) {
        Color oldColor = g2.getColor();
        g2.setColor(currentModel.getColor());
        Mat4 tpm = Mat4.inverseTranspose(mvm);
        Mat4 vpn = m.mul(nsm);   // viewport transformation matrix
        Vec3 lookN = new Vec3(0, 0, -1);
        for (Mesh mesh : currentModel) {
            for (Tri3 t : mesh.tris) {
                Vec3 triN = tpm.mul(t.n).norm();
                if (triN.dot(lookN) > 0)
                    continue;       // cull back faces
                Vec3 v1 = vpn.mul(t.v1);
                Vec3 v2 = vpn.mul(t.v2);
                Vec3 v3 = vpn.mul(t.v3);
                // TODO: shared triangle edges are being rendered twice
                g2.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
                g2.drawLine((int)v2.x, (int)v2.y, (int)v3.x, (int)v3.y);
                g2.drawLine((int)v3.x, (int)v3.y, (int)v1.x, (int)v1.y);
            }
        }
        g2.setColor(oldColor);
    }
    void renderFlat(Graphics2D g2) {
        for (Mesh mesh : currentModel) {
            for (Tri3 t : mesh.tris) {
                // get the triangle's modelview coordinates
                Vec3 v1 = currentModel.getMatrix().mul(mvm).mul(t.v1);
                Vec3 v2 = currentModel.getMatrix().mul(mvm).mul(t.v2);
                Vec3 v3 = currentModel.getMatrix().mul(mvm).mul(t.v3);
                // this normal is used to cull back faces
                Vec3 triN = (Vec3.sub(v2, v1).cross(Vec3.sub(v3, v1))
                             .norm().neg());
                double d = triN.dot(new Vec3(0, 0, -1));
                if (d < 0)
                    continue;
                // get the triangle's xyz screen coordinates
                Mat4 mm = pjm.mul(nsm);
                v1 = mm.mul(v1);
                v2 = mm.mul(v2);
                v3 = mm.mul(v3);
                // this normal is used to find the pixel depth
                triN = Vec3.sub(v2, v1).cross(Vec3.sub(v3, v1)).norm();
                Vec3 rayP = new Vec3();
                Vec3 rayN = new Vec3(0, 0, -1);
                Vec3 out = new Vec3();
                // triangle 2d bounding box
                Vec4 tribb = Algo.bbox2d(v1, v2, v3);
                // clip the bbox to the window
                tribb.x = Math.max(0, tribb.x);
                tribb.y = Math.max(0, tribb.y);
                tribb.z = Math.min(colorBuf.getWidth() - 1, tribb.z);
                tribb.w = Math.min(colorBuf.getHeight() - 1, tribb.w);
                double triArea = Algo.triArea2d(v1, v2, v3);
                // flat shade, same color for every pixel in this triangle
                int color
                    = Util.scaleColor(currentModel.getColor(), d).getRGB();
                // for each pixel in the triangle's bounding box, left to
                // right, top to bottom.
                for (int y=(int)tribb.y; y<=(int)tribb.w; ++y) {
                    boolean dotting = false;
                    for (int x=(int)tribb.x; x<=(int)tribb.z; ++x) {
                        double b1 = (((y - v3.y) * (v2.x - v3.x) +
                                      (v2.y - v3.y) * (v3.x - x))
                                     / triArea);
                        double b2 = (((y - v1.y) * (v3.x - v1.x) +
                                      (v3.y - v1.y) * (v1.x - x))
                                     / triArea);
                        double b3 = (((y - v2.y) * (v1.x - v2.x) +
                                      (v1.y - v2.y) * (v2.x - x))
                                     / triArea);
                        if (b1 >= 0 && b1 <= 1 &&
                            b2 >= 0 && b2 <= 1 &&
                            b3 >= 0 && b3 <= 1) {
                            // pixel is in the triangle.
                            rayP.set(x, y, 1); // near plane at 1
                            Algo.xsectRayPlane(v1, triN, rayP, rayN, out);
                            if (depthBuf.testAndSet(out.z, x, y))
                                colorBuf.setRGB(x, y, color);
                            dotting = true;
                        }
                        else if (dotting)
                            // exited right side of the triangle
                            break;
                    }
                }
            }
        }
    }
    void renderSmooth(Graphics2D g2) {
        for (Mesh mesh : currentModel) {
            for (Tri3 t : mesh.tris) {
                // get the triangle's modelview coordinates
                // get the triangle's modelview coordinates
                Vec3 v1 = currentModel.getMatrix().mul(mvm).mul(t.v1);
                Vec3 v2 = currentModel.getMatrix().mul(mvm).mul(t.v2);
                Vec3 v3 = currentModel.getMatrix().mul(mvm).mul(t.v3);
                // Vec3 v1 = mvm.mul(t.v1);
                // Vec3 v2 = mvm.mul(t.v2);
                // Vec3 v3 = mvm.mul(t.v3);
                // this normal is used to cull back faces
                Vec3 triN = (Vec3.sub(v2, v1).cross(Vec3.sub(v3, v1))
                             .norm().neg());
                double d = triN.dot(new Vec3(0, 0, -1));
                if (d < 0)
                    continue;
                // get the triangle's xyz screen coordinates
                Mat4 mm = pjm.mul(nsm);
                v1 = mm.mul(v1);
                v2 = mm.mul(v2);
                v3 = mm.mul(v3);
                // this normal is used to find the pixel depth
                triN = Vec3.sub(v2, v1).cross(Vec3.sub(v3, v1)).norm();
                // these normals are used for per-pixel lighting
                Mat4 tpm = Mat4.inverseTranspose(mvm);
                Vec3 n1 = tpm.mul(t.n1).norm();
                Vec3 n2 = tpm.mul(t.n2).norm();
                Vec3 n3 = tpm.mul(t.n3).norm();
                // for depth buffer checking
                Vec3 rayP = new Vec3(); // next pixel location
                Vec3 rayN = new Vec3(0, 0, -1);
                Vec3 out = new Vec3();
                // triangle 2d bounding box
                Vec4 tribb = Algo.bbox2d(v1, v2, v3);
                // clip the bbox to the window
                tribb.x = Math.max(0, tribb.x);
                tribb.y = Math.max(0, tribb.y);
                tribb.z = Math.min(colorBuf.getWidth() - 1, tribb.z);
                tribb.w = Math.min(colorBuf.getHeight() - 1, tribb.w);
                double triArea = Algo.triArea2d(v1, v2, v3);
                /*
                  For each pixel in the triangle's bounding box, left to right,
                  top to bottom.
                */
                for (int y=(int)tribb.y; y<=(int)tribb.w; ++y) {
                    boolean dotting = false;
                    // System.out.println("row =============================");
                    for (int x=(int)tribb.x; x<=(int)tribb.z; ++x) {
                        double b1 = (((y - v3.y) * (v2.x - v3.x) +
                                      (v2.y - v3.y) * (v3.x - x))
                                     / triArea);
                        double b2 = (((y - v1.y) * (v3.x - v1.x) +
                                      (v3.y - v1.y) * (v1.x - x))
                                     / triArea);
                        double b3 = (((y - v2.y) * (v1.x - v2.x) +
                                      (v1.y - v2.y) * (v2.x - x))
                                     / triArea);
                        if (b1 >= 0 && b1 <= 1 &&
                            b2 >= 0 && b2 <= 1 &&
                            b3 >= 0 && b3 <= 1) {
                            // pixel is in the triangle.
                            rayP.set(x, y, 1); // at near plane
                            Algo.xsectRayPlane(v1, triN, rayP, rayN, out);
                            if (depthBuf.testAndSet(out.z, x, y)) {
                                /*
                                  now use the vertice normals to calculate the
                                  per/pixel lighting
                                */
                                Vec3 hitNormal =
                                    Vec3.mul(n1, b1).add(Vec3.mul(n2, b2))
                                    .add(Vec3.mul(n3, b3)).norm();
                                int color
                                    = Util.scaleColor(currentModel.getColor(),
                                                      hitNormal
                                                      .dot(new Vec3(0, 0,
                                                                    1)))
                                    .getRGB();
                                colorBuf.setRGB(x, y, color);
                            }
                            dotting = true;
                        }
                        else if (dotting)
                            // exited right side of the triangle
                            break;
                    }
                }
            }
        }
    }    
    void render(Graphics2D g2) {
        // g2.setRenderingHints(rhints);
        g2.setColor(Config.getBackgroundColor());
        g2.fillRect(0, 0, getWidth(), getHeight());
        depthBuf.clear();
        Mat4 m = mvm.mul(pjm);
        for (Model model : models) {
            if (model == null)
                continue;
            currentModel = model;
            switch (renderMode) {
                case WIRE:
                    renderWire(currentModel.getMatrix().mul(m), g2);
                    break;
                case FLAT:
                    renderFlat(g2);
                    break;
                case SMOOTH:
                    renderSmooth(g2);
                    break;
            }
        }
        renderOriginAxis(m, g2);
    }
    /**
       Render x, y, and z lines from the world origin.
       <p>
       The lines are rendered red, green, and blue, respectively. Their length
       will remain constant regardless of zoom.
       </p>
    */
    void renderOriginAxis(Mat4 m, Graphics2D g2) {
        double ps = pixelSize() * 100; // 100 pixel axis length
        Vec3 O = m.mul(nsm).mul(new Vec3());
        Vec3 I = m.mul(nsm).mul(new Vec3(1 * ps, 0, 0));
        Vec3 J = m.mul(nsm).mul(new Vec3(0, 1 * ps, 0));
        Vec3 K = m.mul(nsm).mul(new Vec3(0, 0, 1 * ps));
        g2.setColor(Color.red);
        g2.drawLine((int)O.x, (int)O.y, (int)I.x, (int)I.y);
        g2.setColor(Color.green);
        g2.drawLine((int)O.x, (int)O.y, (int)J.x, (int)J.y);
        g2.setColor(Color.blue);
        g2.drawLine((int)O.x, (int)O.y, (int)K.x, (int)K.y);
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        render(g2i);
        g2.drawImage(colorBuf, 0, 0, null); // swapishly the buffers
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            // wheel back, zoom out
            double sw = wrdH * 1.2;
            wrdW = sw;
            wrdH *= 1.2;
        }
        else if (e.getWheelRotation() < 0) {
            double w = getWidth();
            double h = getHeight();
            Vec2 v = Vec2.sub(mousePos, new Vec2(w / 2, h / 2));
            double dx = v.x / (w * 5);
            double dy = -v.y / (h * 5);
            double sw = wrdW * 0.9;
            // TODO: check for min zoom
            wrdW = sw;
            wrdH *= .9;
            wrdC.set(wrdC.x + dx * wrdW,
                     wrdC.y + dy * wrdH);
        }
        ortho();
        repaint();
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (downButton == MouseEvent.BUTTON1) {
            Vec2 v = new Vec2(e.getX(), e.getY()).sub(mousePos);
            rotateScene(new Vec3(v.y, v.x, 0), rotFactor * v.mag());
            mousePos.set(e.getX(), e.getY());
            repaint();
        }
        else if (downButton == MouseEvent.BUTTON2) {
            Vec2 v = new Vec2(e.getX(), e.getY()).sub(mousePos);
            pan(v.x, v.y);
            mousePos.set(e.getX(), e.getY());
            repaint();
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos.set(e.getX(), e.getY());
    }
    @Override public void mousePressed(MouseEvent e) {
        downButton = e.getButton();
    }
    @Override public void mouseReleased(MouseEvent e) {
        downButton = 0;
    }
    /* ----------------------------------------------------------------------
       Fixed views
    */
    void frontView() {
        mvm = new Mat4();           // identity
        fitAll();
    }
    void topView() {
        mvm = Mat4.rotX(Math.toRadians(90));
        fitAll();
    }
    void rightView() {
        mvm = Mat4.rotY(Math.toRadians(-90));
        fitAll();
    }
    void leftView() {
        mvm = Mat4.rotY(Math.toRadians(90));
        fitAll();
    }
    void backView() {
        mvm = Mat4.rotY(Math.toRadians(180));
        fitAll();
    }
    void bottomView() {
        mvm = Mat4.rotX(Math.toRadians(-90));
        fitAll();
    }
    void isometricView() {
        mvm = Mat4.rotY(Math.toRadians(-45)).mul(Mat4.rotX(isoAngle));
        fitAll();
    }
    // ======================================================================
    // Model Changed
    // ======================================================================
    public void adapterChanged(Model model, int spindle) {
        System.out.println("adapterChanged");
        if (spindle == 1) {
            model.setMatrix(Mat4.translate(5, 0, 0));
            models[SP1_ADAPT_IDX] = model;
        }
        else {
            model.setMatrix(Mat4.translate(-5, 0, 0));
            models[SP2_ADAPT_IDX] = model;
        }
        fitAll();
    }
    public void wheelChanged(Model model, int spindle, int wheel) {
        System.out.println("wheelChanged");
        if (spindle == 1) {
            switch (wheel) {
                case 1:
                    models[SP1_WHEEL1_IDX] = model;
                    break;
                case 2:
                    models[SP1_WHEEL2_IDX] = model;
                    break;
                case 3:
                    models[SP1_WHEEL3_IDX] = model;
                    break;
            }
        }
        else {
            switch (wheel) {
                case 1:
                    models[SP2_WHEEL1_IDX] = model;
                    break;
                case 2:
                    models[SP2_WHEEL2_IDX] = model;
                    break;
                case 3:
                    models[SP2_WHEEL3_IDX] = model;
                    break;
            }
        }
        fitAll();
    }
    //
    public void chuckChanged(Model model) {
        System.out.println("chuckChanged");
        models[CHUCK_IDX] = model;
        fitAll();
    }
    public void colletChanged(Model model) {
        System.out.println("colletChanged");
        models[COLLET_IDX] = model;
        fitAll();
    }
    public void partChanged(Model model) {
        System.out.println("partChanged");
        models[PART_IDX] = model;
        fitAll();
    }
    /* ----------------------------------------------------------------------
       Unused ComponentListener methods
    */
    @Override public void componentHidden(ComponentEvent e) {}
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
    /* ----------------------------------------------------------------------
       Unused MouseListener methods
    */
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
}

@SuppressWarnings("serial")
class SimPopupMenu extends JPopupMenu implements ActionListener {
    SimView view;
    SimPopupMenu(SimView view) {
        this.view = view;
        JMenu viewMenu = new JMenu("View");
        add(viewMenu);
        JMenu renderMenu = new JMenu("Render");
        add(renderMenu);
        JMenuItem mi;
        // view menu
        for (String label : new String[] {
                "Fit", "Front", "Top", "Right", "Left", "Back", "Bottom",
                "Isometric"}) {
            mi = new JMenuItem(label);
            mi.addActionListener(this);
            viewMenu.add(mi);    
        }
        for (String label : new String[] {"Wire", "Flat", "Smooth"}) {
            mi = new JMenuItem(label);
            mi.addActionListener(this);
            renderMenu.add(mi);    
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            // view
            case "Fit": view.fitAll(); break;
            case "Front": view.frontView(); break;
            case "Top": view.topView(); break;
            case "Right": view.rightView(); break;
            case "Left": view.leftView(); break;
            case "Back": view.backView(); break;
            case "Bottom": view.bottomView(); break;
            case "Isometric": view.isometricView(); break;
            case "Wire": view.setRenderMode(RenderMode.WIRE); break;
            case "Flat": view.setRenderMode(RenderMode.FLAT); break;
            case "Smooth": view.setRenderMode(RenderMode.SMOOTH); break;
        }
    }
    
}

class DepthBuffer {
    double a[][];
    double minDepth;
    int width, height;
    DepthBuffer(int width, int height) {
        this(width, height, -1);
    }
    DepthBuffer(int width, int height, double minDepth) {
        this.width = width;
        this.height = height;
        this.minDepth = minDepth;
        a = new double[width][height];
        clear();
    }
    /**
       Find if a given pixel is in front of another.

       @param z pixel depth in NDC
       @param x pixel x coordinate
       @param y pixel y coordinate
       @return true if z is in front of the value at x and y
    */
    boolean testAndSet(double z, int x, int y) {
        if (a[x][y] < z) {
            a[x][y] = z;
            return true;
        }
        return false;
    }
    void clear() {
        for (int i=0; i<width; ++i)
            for (int j=0; j<height; ++j)
                a[i][j] = minDepth;
    }
}
