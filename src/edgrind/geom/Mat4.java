/*
  Mat4.java
  S. Edward Dolan
  Thursday, November 16 2023
*/

package edgrind.geom;

import java.util.Stack;
// 
import edgrind.error.ZeroError;

public class Mat4 {
    /** Single matrix stack per process */
    static Stack<Mat4> stack = new Stack<Mat4>();
    static {
        stack.push(new Mat4());
    }
    public double a[][];             // rows/cols
    /**
       Construct a 4x4 identity matrix.
    */
    public Mat4() {
        a = new double[][] {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }
    public Mat4(double a[][]) {
        this.a = a;
    }
    public Mat4(Mat4 m) {
        a = new double[4][4];
        for (int i=0; i<4; ++i)
            for (int j=0; j<4; ++j)
                a[i][j] = m.a[i][j];
    }
    public String toString() {
        return String.format("Mat4 %+-15.8f %+-15.8f %+-15.8f %+-15.8f\n" +
                             "     %+-15.8f %+-15.8f %+-15.8f %+-15.8f\n" +
                             "     %+-15.8f %+-15.8f %+-15.8f %+-15.8f\n" +
                             "     %+-15.8f %+-15.8f %+-15.8f %+-15.8f",
                             a[0][0], a[1][0], a[2][0], a[3][0],
                             a[0][1], a[1][1], a[2][1], a[3][1],
                             a[0][2], a[1][2], a[2][2], a[3][2],
                             a[0][3], a[1][3], a[2][3], a[3][3]);
    }
    public Mat4 mul(Mat4 m) {
        Mat4 out = new Mat4();
        for (int i=0; i<4; ++i)
            for (int j=0; j<4; ++j) {
                double num = 0;
                for (int k=0; k<4; ++k)
                    num += a[i][k] * m.a[k][j];
                out.a[i][j] = num;
            }
        return out;
    }
    public Vec3 mul(Vec3 v, boolean normal) {
        double[] res = new double[4];
        double[] vv = new double[] {v.x, v.y, v.z, normal ? 0.0 : 1.0};
        for (int r=0; r<4; ++r) {
            double prod = 0;
            for (int c=0; c<4; ++c)
                prod += vv[c] * a[c][r];
            res[r] = prod;
        }
        // System.out.println("w:" + res[3]);
        return new Vec3(res[0], res[1], res[2]);
    }
    public Vec3 mul(Vec3 v) {
        return mul(v, false);
    }
    public Mat4 div(double s) {
        for (int i=0; i<4; ++i)
            for (int j=0; j<4; ++j)
                a[i][j] /= s;
        return this;
    }
    /**
       Transform the triangle's vertices by this matrix.
       @param t the triangle to transform
       @return a new Tri3

       TODO: this thing is buggy, it somehow mutates the passed Tri3
    */
    // public Tri3 mul(Tri3 t) {
    //     return new Tri3(mul(t.v1), mul(t.v2), mul(t.v3), t.n1, t.n2, t.n3);
    // }
    public Vec4 row(int i) {
        return new Vec4(a[i][0], a[i][1], a[i][2], a[i][3]);
    }
    public Vec4 col(int i) {
        return new Vec4(a[0][i], a[1][i], a[2][i], a[3][i]);
    }
    /**
       Create a new 4x4 translation matrix.
       @param x the units to translate along the x axis
       @param y the units to translate along the y axis
       @param z the units to translate along the z axis
    */
    public static Mat4 translate(double x, double y, double z) {
        return new Mat4(new double[][] {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {x, y, z, 1},
            });
    }
    /**
       Create a new 4x4 translation matrix.
       @param v the x, y, and z units to translate
    */
    public static Mat4 translate(Vec3 v) {
        return new Mat4(new double[][] {
                {  1,   0,   0, 0},
                {  0,   1,   0, 0},
                {  0,   0,   1, 0},
                {v.x, v.y, v.z, 1},
            });
    }
    /**
       Create a new 4x4 translation matrix.
       @param m the matrix to translate, not modified
       @param v the x, y, and z units to translate
    */
    public static Mat4 translate(Mat4 m, Vec3 v) {
        Mat4 out = new Mat4(m);
        Vec4 a = Vec4.mul(m.row(0), v.x);
        Vec4 b = Vec4.mul(m.row(1), v.y);
        Vec4 c = Vec4.mul(m.row(2), v.z);
        Vec4 d = a.add(b).add(c).add(out.row(3));
        out.a[3][0] = d.x;
        out.a[3][1] = d.y;
        out.a[3][2] = d.z;
        out.a[3][3] = d.w;
        return out;
    }
    /**
       Create a new Mat4 rotated about the X axis.
       @param angle the angle to rotate, in radians
    */
    public static Mat4 rotX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Mat4(new double[][] {
                {1,  0, 0, 0},
                {0,  c, s, 0},
                {0, -s, c, 0},
                {0,  0, 0, 1}
            });
    }
    /**
       Create a new Mat4 rotated about the Y axis.
       @param angle the angle to rotate, in radians
    */
    public static Mat4 rotY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Mat4(new double[][] {
                {c, 0, -s, 0},
                {0, 1,  0, 0},
                {s, 0,  c, 0},
                {0, 0,  0, 1}
            });
    }
    /**
       Create a new Mat4 rotated about the Z axis.
       @param angle the angle to rotate, in radians
    */
    public static Mat4 rotZ(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Mat4(new double[][] {
                { c, s, 0, 0},
                {-s, 0, c, 0},
                { 0, 0, 1, 0},
                { 0, 0, 0, 1}
            });
    }
    /**
       Create a 4x4 matrix rotated about an axis.
       <p>The method will normalize the axis.
       @param axis the axis to rotate about
       @param angle the angle (in radians) to rotate
       </p>
    */
    public static Mat4 axisAngle(Vec3 axis, double angle) {
        try {
            axis = Vec3.norm(axis);
        }
        catch (ZeroError e) {
            return new Mat4();
        }
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        axis = Vec3.norm(axis);
        Vec3 temp = Vec3.mul(axis, 1 - c);
        Mat4 m = new Mat4();
        // 
        m.a[0][0] = c + temp.x * axis.x;
        m.a[0][1] = temp.x * axis.y + s * axis.z;
        m.a[0][2] = temp.x * axis.z - s * axis.y;
        //
        m.a[1][0] = temp.y * axis.x - s * axis.z;
        m.a[1][1] = c + temp.y * axis.y;
        m.a[1][2] = temp.y * axis.z + s * axis.x;
        //
        m.a[2][0] = temp.z * axis.x + s * axis.y;
        m.a[2][1] = temp.z * axis.y - s * axis.x;
        m.a[2][2] = c + temp.z * axis.z;
        return m;
    }
    /**
       Create a new 4x4 orthographic projection matrix.
       <pre>
       .        2             
       .       ---       0       0       0
       .       r-l                
       .         
       .                 2        
       .        0       ---      0       0
       .                t-b       
       .                                   
       .                         2 
       .        0        0      ---      0
       .                        f-n
       .                        
       .       r+l      t+b     f+n
       .     - ---    - ---   - ---      1
       .       r-l      t-b     f-n
       </pre>
       @param left the left clipping plane
       @param right the right clipping plane
       @param bottom the bottom clipping plane
       @param top the top clipping plane
       @param near the near clipping plane
       @param far the far clipping plane
    */
    public static Mat4 ortho(double left, double right, double bottom,
                             double top, double near, double far) {
        Mat4 m = new Mat4();
        m.a[0][0] = 2. / (right - left);
        m.a[1][1] = 2. / (top - bottom);
        m.a[2][2] = -2 / (far - near);
        m.a[3][0] = -((right + left) / (right - left));
        m.a[3][1] = -((top + bottom) / (top - bottom));
        m.a[3][2] = -((far + near) / (far - near));
        return m;
    }
    public static Mat4 inverseTranspose(Mat4 m) {
        double[][] a = m.a;
        double sf00 = a[2][2] * a[3][3] - a[3][2] * a[2][3];
        double sf01 = a[2][1] * a[3][3] - a[3][1] * a[2][3];
        double sf02 = a[2][1] * a[3][2] - a[3][1] * a[2][2];
        double sf03 = a[2][0] * a[3][3] - a[3][0] * a[2][3];
        double sf04 = a[2][0] * a[3][2] - a[3][0] * a[2][2];
        double sf05 = a[2][0] * a[3][1] - a[3][0] * a[2][1];
        double sf06 = a[1][2] * a[3][3] - a[3][2] * a[1][3];
        double sf07 = a[1][1] * a[3][3] - a[3][1] * a[1][3];
        double sf08 = a[1][1] * a[3][2] - a[3][1] * a[1][2];
        double sf09 = a[1][0] * a[3][3] - a[3][0] * a[1][3];
        double sf10 = a[1][0] * a[3][2] - a[3][0] * a[1][2];
        double sf11 = a[1][0] * a[3][1] - a[3][0] * a[1][1];
        double sf12 = a[1][2] * a[2][3] - a[2][2] * a[1][3];
        double sf13 = a[1][1] * a[2][3] - a[2][1] * a[1][3];
        double sf14 = a[1][1] * a[2][2] - a[2][1] * a[1][2];
        double sf15 = a[1][0] * a[2][3] - a[2][0] * a[1][3];
        double sf16 = a[1][0] * a[2][2] - a[2][0] * a[1][2];
        double sf17 = a[1][0] * a[2][1] - a[2][0] * a[1][1];
        //
        Mat4 out = new Mat4();
        out.a[0][0] = + (a[1][1] * sf00 - a[1][2] * sf01 + a[1][3] * sf02);
        out.a[0][1] = - (a[1][0] * sf00 - a[1][2] * sf03 + a[1][3] * sf04);
        out.a[0][2] = + (a[1][0] * sf01 - a[1][1] * sf03 + a[1][3] * sf05);
        out.a[0][3] = - (a[1][0] * sf02 - a[1][1] * sf04 + a[1][2] * sf05);
        // 
        out.a[1][0] = - (a[0][1] * sf00 - a[0][2] * sf01 + a[0][3] * sf02);
        out.a[1][1] = + (a[0][0] * sf00 - a[0][2] * sf03 + a[0][3] * sf04);
        out.a[1][2] = - (a[0][0] * sf01 - a[0][1] * sf03 + a[0][3] * sf05);
        out.a[1][3] = + (a[0][0] * sf02 - a[0][1] * sf04 + a[0][2] * sf05);
        // 
        out.a[2][0] = + (a[0][1] * sf06 - a[0][2] * sf07 + a[0][3] * sf08);
        out.a[2][1] = - (a[0][0] * sf06 - a[0][2] * sf09 + a[0][3] * sf10);
        out.a[2][2] = + (a[0][0] * sf07 - a[0][1] * sf09 + a[0][3] * sf11);
        out.a[2][3] = - (a[0][0] * sf08 - a[0][1] * sf10 + a[0][2] * sf11);
        // 
        out.a[3][0] = - (a[0][1] * sf12 - a[0][2] * sf13 + a[0][3] * sf14);
        out.a[3][1] = + (a[0][0] * sf12 - a[0][2] * sf15 + a[0][3] * sf16);
        out.a[3][2] = - (a[0][0] * sf13 - a[0][1] * sf15 + a[0][3] * sf17);
        out.a[3][3] = + (a[0][0] * sf14 - a[0][1] * sf16 + a[0][2] * sf17);
        // 
        double d =
            + a[0][0] * out.a[0][0]
            + a[0][1] * out.a[0][1]
            + a[0][2] * out.a[0][2]
            + a[0][3] * out.a[0][3];
        // 
        return out.div(d);
    }
    /**
       Get a matrix that will map NDC coords to screen coords.
       
       @param v the viewport vector [x-origin, y-origin, width, height]
       @return a new Mat4
    */
    public static Mat4 ndcToScreen(Vec4 v) {
        return new Mat4(new double[][] {
                {      v.z / 2,             0, 0, 0},
                {            0,      -v.w / 2, 0, 0},
                {            0,             0, 1, 0},
                {(v.z - 1) / 2, (v.w - 1) / 2, 0, 1}
            });
    }
    // ======================================================================
    // Stack Opts
    static public void pushMatrix() {
        stack.push(stack.peek());
    }
    static public Mat4 popMatrix() {
        return stack.pop();
    }
    static public void loadMatrix(Mat4 m) {
        stack.set(stack.size() - 1, m);
    }
    static public Mat4 mulMatrix(Mat4 m) {
        return stack.set(stack.size() - 1, stack.peek().mul(m));
    }
    static public Mat4 topMatrix() {
        return stack.peek();
    }
}
