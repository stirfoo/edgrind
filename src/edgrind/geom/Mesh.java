/*
  Mesh.java
  S. Edward Dolan
  Saturday, November 18 2023
*/

package edgrind.geom;

import java.util.List;
import java.util.ArrayList;
//
import java.awt.Color;
//
import edgrind.error.IllegalStateError;
import edgrind.error.IllegalArgumentError;

/**
   A collection of triangles.
 */
public class Mesh {
    protected static final int N_CIRCLE_SEGS = 32;
    // 
    public List<Tri3> tris;
    // 
    protected List<Vec3> verts;
    protected List<Vec3> norms;
    Mesh() {
        tris = new ArrayList<Tri3>();
        verts = new ArrayList<Vec3>();
        norms = new ArrayList<Vec3>();
    }
    public AABBox getBBox(Mat4 m) {
        List<Vec3> vs = new ArrayList<Vec3>();
        for (Vec3 v : verts) {
            vs.add(m.mul(v));
            vs.add(m.mul(v));
            vs.add(m.mul(v));
        }
        return AABBox.fromVertices(vs);
    }
    /**
       Add the vertex to the verts array if it is not already present.
       <p>Vertices are compared by Vec3.equals<p>
       @param v the vertex to add
       @return the vertex in the array
    */
    Vec3 addVert(Vec3 v) {
        int i = verts.indexOf(v);
        if (i == -1) {
            verts.add(v);
            i = verts.size() - 1;
        }
        return verts.get(i);
    }
    Vec3 addVert(double x, double y, double z) {
        return addVert(new Vec3(x, y, z));
    }
    /**
       Add the normals to the norms array if not already present.
       @param ns the normals to add
       @return the normal in the array
    */
    Vec3 addNormal(Vec3 n) {
        int i = norms.indexOf(n);
        if (i == -1) {
            norms.add(n);
            i = norms.size() - 1;
        }
        return norms.get(i);
    }
    void addTri(Vec3 v1, Vec3 v2, Vec3 v3) {
        tris.add(new Tri3(v1, v2, v3));
    }
    void addTri(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 n) {
        tris.add(new Tri3(v1, v2, v3, n));
    }
    void addTri(Vec3 v1, Vec3 v2, Vec3 v3, Color color) {
        tris.add(new Tri3(v1, v2, v3, color));
    }
    void addTri(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 n1, Vec3 n2, Vec3 n3) {
        tris.add(new Tri3(v1, v2, v3, n1, n2, n3));
    }
    /**
       
     */
    void addQuad(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4) {
        addTri(p1, p2, p3);
        addTri(p4, p1, p3);
    }
    void addQuad(Vec3 p1, Vec3 n1,
                 Vec3 p2, Vec3 n2,
                 Vec3 p3, Vec3 n3,
                 Vec3 p4, Vec3 n4) {
        addTri(p1, p2, p3, n1, n2, n3);
        addTri(p4, p1, p3, n4, n1, n3);
    }
    // ======================================================================
    // Cube
    // ======================================================================
    /**
       Create a unit cube containig 12 triangles.
       <p>The cube will be centered at x=0, y=0, z=0.
       <pre>
       .     7 o-------o 6
       .      /|      /|             Y+
       .     / |     / |             |
       .  3 o-------o 2|             |
       .    |8 o----|--o 5           o-----> X+
       .    | /     | /             /
       .    |/      |/             Z+
       .  4 o-------o 1           
       </pre>
       </p>
    */
    public static Mesh newCube() {
        return Mesh.newCube(1);
    }
    public static Mesh newCube(double size) {
        Mesh m = new Mesh();
        double s = size / 2;
        double t = s / 2;
        // front vertices
        Vec3 v1 = m.addVert(new Vec3(s, -s, s));
        Vec3 v2 = m.addVert(new Vec3(s, s, s));
        Vec3 v3 = m.addVert(new Vec3(-s, s, s));
        Vec3 v4 = m.addVert(new Vec3(-s, -s, s));
        // back vertices
        Vec3 v5 = m.addVert(new Vec3(s, -s, -s));
        Vec3 v6 = m.addVert(new Vec3(s, s, -s));
        Vec3 v7 = m.addVert(new Vec3(-s, s, -s));
        Vec3 v8 = m.addVert(new Vec3(-s, -s, -s));
        // 
        // front face
        m.addTri(v1, v2, v3);
        m.addTri(v3, v4, v1);
        // back face
        m.addTri(v8, v7, v6);
        m.addTri(v6, v5, v8);
        // right face
        m.addTri(v5, v6, v2);
        m.addTri(v2, v1, v5);
        // left face
        m.addTri(v4, v3, v7);
        m.addTri(v7, v8, v4);
        // top face
        m.addTri(v2, v6, v7);
        m.addTri(v7, v3, v2);
        // bottom face
        m.addTri(v1, v4, v8);
        m.addTri(v8, v5, v1);
        return m;
    }
    // ======================================================================
    // Cylinder
    // ======================================================================
    /**
       Create a cylinder mesh.

       The cylinder is extruded along the Z+ axis with its origin at 0,0,0.

       <p>
       Two triangle making the first side.
       <pre>
       .    Z+
       .    ^
       .    |
       . p1 *---* p3
       .    |  /|
       .    | / |
       .    |/  | p4
       . p2 O---* ----> Y+
       </pre>
       </p>
    */
    public static Mesh cylinder(double radius, double height, int nSides,
                                Mat4 m) {
        double incAngle = Math.PI * 2 / nSides, a;
        Vec3 p1, p2, p3, p4, n2, n4;
        Mat4 itm = Mat4.inverseTranspose(m);
        Mesh mesh = new Mesh();
        for (int i=0; i<nSides; ++i) {
            a = incAngle * i;
            p1 = new Vec3(radius * Math.cos(a), radius * Math.sin(a), height);
            p2 = new Vec3(p1.x, p1.y, 0);
            n2 = mesh.addNormal(itm.mul(p2).norm());
            p1 = mesh.addVert(m.mul(p1));
            p2 = mesh.addVert(m.mul(p2));
            // 
            a = incAngle * (i + 1);
            p3 = new Vec3(radius * Math.cos(a), radius * Math.sin(a), height);
            p4 = new Vec3(p3.x, p3.y, 0);
            n4 = mesh.addNormal(itm.mul(p4).norm());
            p3 = mesh.addVert(m.mul(p3));
            p4 = mesh.addVert(m.mul(p4));
            mesh.addTri(p1, p2, p3, n2, n2, n4);
            mesh.addTri(p3, p2, p4, n4, n2, n4);
        }
        return mesh;
    }
    public static Mesh cylinder() {
        return cylinder(1, 1, N_CIRCLE_SEGS, new Mat4());
    }
    public static Mesh cylinder(double radius) {
        return cylinder(radius, 1, N_CIRCLE_SEGS, new Mat4());
    }
    public static Mesh cylinder(double radius, double height) {
        return cylinder(radius, height, N_CIRCLE_SEGS, new Mat4());
    }
    public static Mesh cylinder(double radius, double height, Mat4 m) {
        return cylinder(radius, height, N_CIRCLE_SEGS, m);
    }
    // ======================================================================
    // Disk
    // ======================================================================
    /**
       Create a flat, round-ish disk.
       
       <p>If m is the identity matrix, the disk will be created in the xy
       plane. The Z+ will be the <em>front</em> side.</p>
       
       @param radius the radius of the disk
       @param nSides number of sides around the circumference of the disk
       @param m the transformation matrix
    */
    public static Mesh disk(double radius, int nSides, Mat4 m) {
        Mesh mesh = new Mesh();
        double incAngle = Math.PI * 2 / nSides, a = 0;
        Vec3 v1 = mesh.addVert(m.mul(new Vec3()));
        Vec3 v2 = mesh.addVert(m.mul(new Vec3(radius, 0, 0)));
        Vec3 v3 = null;
        for (int i=1; i<=nSides; ++i) {
            a = incAngle * i;
            v3 = mesh.addVert(m.mul(new Vec3(radius * Math.cos(a),
                                             radius * Math.sin(a),
                                             0)));
            mesh.addTri(v1, v2, v3);
            v2 = v3;
        }
        return mesh;
    }
    public static Mesh disk(double radius) {
        return disk(radius, N_CIRCLE_SEGS);
    }
    public static Mesh disk(double radius, Mat4 m) {
        return disk(radius, N_CIRCLE_SEGS, m);
    }
    public static Mesh disk(double radius, int nSides) {
        return disk(radius, nSides, new Mat4());
    }
    /**
       Rotate the line seg adding a disk, cone, or cylindric to this mesh.
       <p>
       p1 and p2 should lay in the y/z plane. The axis of revolution will be
       the z axis. The surface will be rotated ccw looking down the z- axis
       with normals pointing outward.
       </p>
       @param v1 the first point on the line seg
       @param v2 the second point on the line seg
    */
    private void addRevLineSeg(Vec3 v1, Vec3 v2) {
        double incAngle = Math.PI * 2 / N_CIRCLE_SEGS, a = 0;
        double y1 = v1.y, z1 = v1.z, y2 = v2.y, z2 = v2.z, tmp;
        if (Eps.zero(y1) || Eps.zero(y2)) {
            if (Eps.eq(z1, z2)) {
                // disk with no hole
                boolean rev = Eps.zero(y2);
                double r = Math.abs(y2 - y1);
                // disk center
                Vec3 p1 = addVert(0, rev ? y2 : y1, z1);
                // first point on circumference
                Vec3 p2 = addVert(r, 0, z1);
                Vec3 p3 = null;
                for (int i=1; i<=N_CIRCLE_SEGS; ++i) {
                    a = incAngle * i;
                    p3 = addVert(r * Math.cos(a), r * Math.sin(a), z1);
                    if (rev)
                        addTri(p1, p2, p3);
                    else
                        addTri(p1, p3, p2);
                    p2 = p3;
                }
            }
            else {
                // cone with apex on the z axis
            }
        }
        else if (Eps.eq(y1, y2)) {
            // cylinder
            if (Eps.lt(z2, z1)) {
                // make z1 < z2
                tmp = z2;
                z2 = z1;
                z1 = tmp;
            }
            double r = y1;
            Vec3 p1, p2, p3, p4, n1, n2;
            for (int i=0; i<N_CIRCLE_SEGS; ++i) {
                a = incAngle * i;
                p1 = addVert(r * Math.cos(a), r * Math.sin(a), z2);
                p2 = addVert(p1.x, p1.y, z1);
                n1 = addNormal(new Vec3(p1.x, p1.y, 0).norm());
                // 
                a = incAngle * (i + 1);
                p3 = addVert(r * Math.cos(a), r * Math.sin(a), z1);
                p4 = addVert(p3.x, p3.y, z2);
                n2 = addNormal(new Vec3(p3.x, p3.y, 0).norm());
                addTri(p1, p2, p3, n1, n1, n2);
                addTri(p3, p4, p1, n2, n2, n1);
            }
        }
        else if (z1 == z2) {
            // disk with a hole in it
            boolean rev = Eps.lt(y2, y1);
            double r1 = rev ? y2 : y1; // little r
            double r2 = rev ? y1 : y2; // big r
            Vec3 p1, p2, p3, p4;
            for (int i=0; i<=N_CIRCLE_SEGS; ++i) {
                a = incAngle * i;
                double c = Math.cos(a);
                double s = Math.sin(a);
                p1 = addVert(r1 * c, r1 * s, z1);
                p2 = addVert(r2 * c, r2 * s, z1);
                a = incAngle * (i + 1);
                c = Math.cos(a);
                s = Math.sin(a);
                p3 = addVert(r2 * c, r2 * s, z1);
                p4 = addVert(r1 * c, r1 * s, z1);
                if (rev) {
                    addTri(p1, p2, p3);
                    addTri(p3, p4, p1);
                }
                else {
                    addTri(p1, p3, p2);
                    addTri(p1, p4, p3);
                }
            }
        }
        else {
            // truncated cone
            Vec3 lineN = new Vec3(v2, v1).norm();
            if (Eps.lt(z2, z1)) {
                // make z1 < z2
                tmp = z2;
                z2 = z1;
                z1 = tmp;
                tmp = y2;
                y2 = y1;
                y1 = tmp;
                lineN.neg();
            }
            Mat4 m;
            double c, s;
            Vec3 linePerp = new Vec3(-lineN.z, 0, lineN.y);
            Vec3 zaxis = new Vec3(0, 0, 1);
            boolean rev = Eps.lt(y2, y1);
            double r1 = rev ? y2 : y1;
            double r2 = rev ? y1 : y2;
            Vec3 p1, p2, p3, p4, n1, n2;
            for (int i=0; i<N_CIRCLE_SEGS; ++i) {
                a = incAngle * i;
                m = Mat4.axisAngle(zaxis, a);
                n1 = addNormal(m.mul(linePerp).norm());
                c = Math.cos(a);
                s = Math.sin(a);
                p1 = addVert(r1 * c, r1 * s, z2);
                p2 = addVert(r2 * c, r2 * s, z1);
                a = incAngle * (i + 1);
                m = Mat4.axisAngle(zaxis, a);
                n2 = addNormal(m.mul(linePerp).norm());
                c = Math.cos(a);
                s = Math.sin(a);
                p3 = addVert(r2 * c, r2 * s, z1);
                p4 = addVert(r1 * c, r1 * s, z2);
                addTri(p1, p2, p3, n1, n1, n2);
                addTri(p3, p4, p1, n2, n2, n1);
            }
        }
    }
    /**
       Add a revolved arc patch to this mesh.
       <p>
       The arc must have a clock-wise orientation.
       </p>
     */
    public void addRevArc(Arc2 arc) {
        // arc parameters
        Vec2 sp = arc.startPt();
        Vec2 ep = arc.endPt();
        Vec2 cp = arc.centerPt();
        double angStep = 360 / N_CIRCLE_SEGS;
        int segs = Math.max((int)(Math.abs(arc.sweepAngle()) / angStep), 3);
        double step = arc.sweepAngle() / segs;
        double sa = arc.startAngle();
        double a1 = Math.toRadians(sa);
        double sa1 = Math.sin(a1);
        double ca1 = Math.cos(a1);
        double a2, sa2, ca2, x1, y1, x2, y2;
        // patch parameters
        double incAngle = Math.PI * 2 / N_CIRCLE_SEGS, a, c, s;
        Vec3 p1, p2, p3, p4;
        Vec3 cp3 = new Vec3(cp.y, 0, cp.x), cp3t;
        Vec3 zaxis = new Vec3(0, 0, 1);
        Vec3 n1, n2, n3, n4;
        Mat4 m;
        for (int i=1; i<=segs; ++i) {
            a2 = Math.toRadians(sa + step * i);
            sa2 = Math.sin(a2);
            ca2 = Math.cos(a2);
            x1 = arc.cx + arc.r * ca1;
            y1 = arc.cy + arc.r * sa1;
            x2 = arc.cx + arc.r * ca2;
            y2 = arc.cy + arc.r * sa2;
            // System.out.println("(" + x1 + "," + y1 + ")(" +
            //                    x2 + "," + y2 + ")");
            /*
             x1,y1 and x2,y2 define a line segement along the arc in a
             clockwise direction looking down the Z- axis. This line seg gets
             rotated 90+ degrees about the Y axis onto the YZ plane. So the x
             coord becomes the z coord and the y coord stays the y coord.
            */
            for (int j=0; j<N_CIRCLE_SEGS; ++j) {
                a = incAngle * j;
                c = Math.cos(a);
                s = Math.sin(a);
                p1 = addVert(y1 * c, y1 * s, x1);
                // System.out.println("p1:" + p1);
                p2 = addVert(y2 * c, y2 * s, x2);
                m = Mat4.axisAngle(zaxis, a);
                cp3t = m.mul(cp3);
                // System.out.println("cp3t:" + cp3t);
                n1 = new Vec3(cp3t, p1).norm();
                // System.out.println("n1:" + n1);
                n2 = new Vec3(cp3t, p2).norm();
                // 
                a = incAngle * (j + 1);
                c = Math.cos(a);
                s = Math.sin(a);
                p3 = addVert(y2 * c, y2 * s, x2);
                p4 = addVert(y1 * c, y1 * s, x1);
                m = Mat4.axisAngle(zaxis, a);
                cp3t = m.mul(cp3);
                // System.out.println("cp3t:" + cp3t);
                n3 = new Vec3(cp3t, p3).norm();
                n4 = new Vec3(cp3t, p4).norm();
                // 
                addTri(p1, p4, p3, n1, n4, n3);
                addTri(p3, p2, p1, n3, n2, n1);
            }
            a1 = a2;
            sa1 = sa2;
            ca1 = ca2;
        }
    }
    /**
       Create a new Mesh by revolving the 2d polyline about the z axis.

       @param pts the list of 2 or more points defining the polyline
    */
    public static Mesh revolvePolyline(List<Vec2> pts) {
        Mesh mesh = new Mesh();
        if (pts.size() < 2)
            throw new IllegalStateError("2 or more vertices required in" +
                                        " polyline");
        Vec2 p1 = pts.get(0), p2 = null;
        for (Vec2 v : pts.subList(1, pts.size())) {
            mesh.addRevLineSeg(new Vec3(0, p1.y, p1.x),
                               new Vec3(0, v.y, v.x));
            p1 = v;
        }
        return mesh;
    }
    /**
       Create a new Mesh by revolving the 2d path about the z axis.

       @param elements list of one or more Vec2 and/or Arc2 elements.
    */
    public static Mesh revolvePath(List<Object> elements) {
        // some error handling...
        switch (elements.size()) {
            case 0:
                throw new IllegalStateError("no elements to revolve");
            case 1:
                if (elements.get(0) instanceof Vec2)
                    throw new IllegalStateError("cannot revolve a single" +
                                                " vertex");
                else if (!(elements.get(0) instanceof Arc2))
                    throw new IllegalArgumentError("expected an Arc2" +
                                                   " instance");
                break;
            default:
                break;
        }
        Mesh mesh = new Mesh();
        Object e1 = null, e2 = null;
        for (Object e : elements) {
            if (e1 == null) {
                e1 = e;
                continue;
            }
            e2 = e;
            revElements(mesh, e1, e2);
            if (e1 instanceof Arc2 && e1 != null) {
                e1 = ((Arc2)e1).endPt();
                revElements(mesh, e1, e2);
            }
            e1 = e2;
            e2 = null;
        }
        if (e2 == null)
            revElements(mesh, e1, e2);
        return mesh;
    }
    /**
       Add the revolved segement to the mesh.
       <p>
       The segment will be:
       <ul>
       <li>a line if e1 and e2 are both Vec2</li>
       <li>an arc if e1 is an Arc2, e2 will be ignored
       </ul>
       </p>
    */
    private static void revElements(Mesh mesh, Object e1, Object e2) {
        Arc2 a;
        Vec2 p1, p2;
        if (e1 instanceof Vec2) {
            p1 = (Vec2)e1;
            if (e2 instanceof Vec2) {
                // point to point
                p2 = (Vec2)e2;
                mesh.addRevLineSeg(new Vec3(0, p1.y, p1.x),
                                   new Vec3(0, p2.y, p2.x));
            }
            else if (e2 instanceof Arc2) {
                // point to arc start point
                a = (Arc2)e2;
                p2 = a.startPt();
                mesh.addRevLineSeg(new Vec3(0, p1.y, p1.x),
                                   new Vec3(0, p2.y, p2.x));
            }
            else if (e2 == null)
                ;               // no op
            else
                throw new IllegalArgumentError("expected a Vec2 or Arc2");
        }
        else if (e1 instanceof Arc2)
            mesh.addRevArc((Arc2)e1);
        else
            throw new IllegalArgumentError("expected a Vec2 or Arc2");
    }
}
