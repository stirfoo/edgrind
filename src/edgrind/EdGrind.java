/*
  EdGrind.java
  S. Edward Dolan
  Thursday, July 27 2023
 */

package edgrind;

import java.awt.EventQueue;
import javax.swing.UIManager;
//
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
//
import org.json.JSONObject;
//
import edgrind.geom.*;

class EdGrindRunner implements Runnable {
    String[] args;
    EdGrindRunner(String[] args) {
        this.args = args;
    }
    private void showHelp() {
        System.out.println("EdGrind Comman Line Options\n" +
                           "===========================\n" +
                           "-f, --fullscreen    run full screen\n" +
                           "-h, --help, -?, /?  show this help");
        System.exit(0);
    }
    public void run() {
        try {
            boolean fullScreen = false;
            for (int i=0; i<args.length; ++i) {
                /*
                  So... you can't use (arg == "-f") here because == compares
                  strings by identity, not value. And I suppose the literal
                  "-f" does not get interned into this sessions String cache?
                */
                if (args[i].equals("-h") ||
                    args[i].equals("--help") ||
                    args[i].equals("-?") ||
                    args[i].equals("/?"))
                    showHelp();
                else if (args[i].equals("-f") ||
                         args[i].equals("--fullscreen"))
                    fullScreen = true;
            }
            EdGrind eg = new EdGrind(fullScreen);
            // window.frame.setVisible(true);
            // if (fileName != null)
            //     eg.frame.openProgram(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);     // ???
        }
    }
}

public class EdGrind {
    MainFrame frame;
    public static void main(String[] args) {
        // foo();
        // bar();
        // baz();
        try {
            UIManager
                .setLookAndFeel(UIManager
                                .getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // fuck off
        }
        EventQueue.invokeLater(new EdGrindRunner(args));
    }
    public EdGrind(boolean fullScreen) throws Exception {
        startup();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    shutdown();
                }
            }));
        frame = new MainFrame(fullScreen);
    }
    protected void startup() {
        Resource.loadAll();
    }
    protected void shutdown() {
        Resource.saveAll();
    }
    static protected double calc(Vec3 v1, Vec3 v2, Vec3 v3,
                                 double x, double y) {
        double a = (v1.y * (v2.z - v3.z) +
                    v2.y * (v3.z - v1.z) +
                    v3.y * (v1.z - v2.z));
        double b = (v1.z * (v2.x - v3.x) +
                    v2.z * (v3.x - v1.x) +
                    v3.z * (v1.x - v2.x));
        double c = (v1.x * (v2.y - v3.y) +
                    v2.x * (v3.y - v1.y) +
                    v3.x * (v1.y - v2.y));
        double d = (-v1.x * (v2.y * v3.z - v3.y * v2.z) -
                    v2.x * (v3.y * v1.z - v1.y * v3.z) -
                    v3.x * (v1.y * v2.z - v2.y * v1.z));
        return (d - a * x - b * y) / c;
    }
    static protected void foo() {
        double winW = 640, winH = 480;
        double aspect = winW/winH;
        double wrdH = 10, wrdW = wrdH * aspect;
        double near = 100, far = -100;
        Vec4 vp = new Vec4(0, 0, winW, winH);       // viewport
        Mat4 mvm = Mat4.rotY(Math.toRadians(22.5)); // model view matrix
        Mat4 pjm = Mat4.ortho(-wrdW*.5, wrdW*.5,    // projection matrix
                              -wrdH*.5, wrdH*.5,
                              near, far);
        Mat4 m = mvm.mul(pjm);
        Tri3 tri = new Tri3(new Vec3(0, 0, 0),
                            new Vec3(1, 0, 0),
                            new Vec3(.5, 1, 0));
        Vec3 v1 = m.mul(tri.v1);
        Vec3 v2 = m.mul(tri.v2);
        Vec3 v3 = m.mul(tri.v3);
        System.out.println("v1:" + v1);
        System.out.println("v2:" + v2);
        System.out.println("v3:" + v3);
        double q = (vp.w / wrdH);
        double zd = (near - far) * q;
        System.out.println(" zd:" + zd);
        Vec3 px1 = new Vec3((v1.x + 1) * (vp.z / 2) + vp.x,
                            (v1.y + 1) * (vp.w / 2) + vp.y,
                            (v1.z + 1) * (zd / 2) - near * q);
        Vec3 px2 = new Vec3((v2.x + 1) * (vp.z / 2) + vp.x,
                            (v2.y + 1) * (vp.w / 2) + vp.y,
                            (v2.z + 1) * (zd / 2) - near * q);
        Vec3 px3 = new Vec3((v3.x + 1) * (vp.z / 2) + vp.x,
                            (v3.y + 1) * (vp.w / 2) + vp.y,
                            (v3.z + 1) * (zd / 2) - near * q);
        System.out.println("px1:" + px1);
        System.out.println("px2:" + px2);
        System.out.println("px3:" + px3);
        Vec3 triN = Vec3.sub(px2, px1).cross(Vec3.sub(px3, px1)).norm();
        System.out.println("triN:" + triN);
        Vec3 rayP = new Vec3(px1.x, px1.y, near);
        System.out.println("rayP:" + rayP);
        Vec3 rayN = new Vec3(0, 0, -1);
        System.out.println("rayN:" + rayN);
        Vec3 out = new Vec3();
        if (Algo.xsectRayPlane(px1, triN, rayP, rayN, out))
            System.out.println("out1:" + out);
        rayP.set(px2.x, px2.y, near);
        if (Algo.xsectRayPlane(px2, triN, rayP, rayN, out))
            System.out.println("out2:" + out);
        rayP.set(px3.x, px3.y, near);
        if (Algo.xsectRayPlane(px3, triN, rayP, rayN, out))
            System.out.println("out3:" + out);
        /*
          Mat4 mx = Mat4.axisAngle(new Vec3(1, 0, 0), Math.PI / 6);
          Mat4 my = Mat4.axisAngle(new Vec3(0, 1, 0), Math.PI / 6);
          Mat4 mz = Mat4.axisAngle(new Vec3(0, 0, 1), Math.PI / 6);
          Mat4 mt = Mat4.translate(new Vec3(1, -1, 2));
          Mat4 m1 = mx.mul(my).mul(mz).mul(mt);
          Vec3 v = new Vec3(.3, -1.44, 3.);
          Mat4 m2 = Mat4.ortho(-5, .2, -3.3, 6, -1, 1);
          System.out.println(mx);
          System.out.println(my);
          System.out.println(mz);
          System.out.println(mt);
          System.out.println(m1);
          System.out.println(m1.mul(v));
          System.out.println(m2);
        */
        // Vec3 X = new Vec3(1, 0, 0);
        // Vec3 Y = new Vec3(0, 1, 0);
        // Vec3 Z = new Vec3(0, 0, 1);
        // double angle = 22.5;
        // System.out.println(Mat3.axisAngle(Z, angle));
        // System.out.println(Mat3.rotZ(angle));
    }
    static void bar() {
        Mat4 m1 = Mat4.axisAngle(new Vec3(1, -2, 3), Math.toRadians(-22.4));
        System.out.println(m1);
        m1 = Mat4.translate(m1, new Vec3(-.2, .2, -.3));
        m1 = Mat4.translate(m1, new Vec3(-.2, .2, -.3));
        System.out.println(m1);
        Mat4 m2 = Mat4.inverseTranspose(m1);
        System.out.println(m2);
        System.out.println(m2.mul(new Vec3(1, 2, 3)));
    }
    static void baz() {
        Tri3 t1 = new Tri3(new Vec3(0, 0, 0),
                           new Vec3(1, 0, -1),
                           new Vec3(.5, 1., -.5));
        Mat4 mvm = Mat4.axisAngle(new Vec3(0, 1, 0), Math.toRadians(-45));
        Mat4 pjm = Mat4.ortho(-5, 5, -4.2, 4.2, 1000, -1000);
        Mat4 m = pjm.mul(mvm);
        // Tri3 t2 = m.mul(t1);
        Mat4 it = Mat4.inverseTranspose(mvm);
        System.out.println(it);
        System.out.println(t1.n);
        System.out.println(it.mul(t1.n).norm());
    }
}
