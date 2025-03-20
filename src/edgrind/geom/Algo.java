/*
  Algo.java
  S. Edward Dolan
  Wednesday, September 27 2023
*/

package edgrind.geom;

/**
   Various numeric and geometric algorithms.
 */
public class Algo {
    /**
       Restrict x to the inclusive interval [low, high].
       @return x if x is within [low, high], else low or high
    */
    public static double clamp(double x, double low, double high) {
        return Math.min(high, Math.max(x, low));
    }
    /**
       Find the intersection point of two lines.

       <p><b>NOTE</b>: Taken from the Qt5 QLineF source code. </p>

       @param l1 first line
       @param l2 second line
       @param p the resulting intersection point which must not be null
       @return <ol>
       <li>AlgoEnum.NO_XSECT if no intersection is possible</li>
       <li>AlgoEnum.UNBOUND_XSECT if the parent lines intersect</li>
       <li>AlgoEnum.BOUND_XSECT if the line segments intersect</li>
       </ol>
    */
    public static AlgoEnum xsect(Line2 l1, Line2 l2, Vec2 p) {
        Vec2 a = Vec2.sub(l1.p2(), l1.p1());
        Vec2 b = Vec2.sub(l2.p1(), l2.p2());
        Vec2 c = Vec2.sub(l1.p1(), l2.p1());
        double den = a.y * b.x - a.x * b.y;
        if (den == 0 || Double.isInfinite(den))
            return AlgoEnum.NO_XSECT;
        double recip = 1 / den;
        double na = (b.y * c.x - b.x * c.y) * recip;
        Vec2 xp = Vec2.add(l1.p1(), Vec2.mul(a, na));
        p.set(xp);
        if (na < 0 || na > 1)
            return AlgoEnum.UNBOUND_XSECT;
        double nb = (a.x * c.y - a.y * c.x) * recip;
        if (nb < 0 || nb > 1)
            return AlgoEnum.UNBOUND_XSECT;
        return AlgoEnum.BOUND_XSECT;
    }
    /**
       Find the closest point on the arc to the given reference point.

       @param p the ref point
       @param cp the arc center point
       @param r the arc radius
       @return a Vec2
    */
    public static Vec2 pointOnArc(Vec2 p, Vec2 cp, double r) {
        if (p.equals(cp))
            throw new RuntimeException("p = cp, infinite points found on the" +
                                       " arc");
        Vec2 v = new Vec2(cp, p).norm();
        return Vec2.add(cp, v.mul(r));
    }
    /**
       Find the length of the chord of a circle.
       @param r circle radius
       @param h chord height
    */
    public static double chordLength(double r, double h) {
        return 2 * Math.sqrt(r * r - h * h);
    }
    // TODO: temporary kaka...
    public static boolean sameSide(Vec3 a, Vec3 b, Vec3 c, Vec3 p) {
        Vec3 a2b = new Vec3(a, b);
        Vec3 a2c = new Vec3(a, c);
        Vec3 a2p = new Vec3(a, p);
        double x = a2b.x * a2c.y - a2c.x * a2b.y;
        double y = a2b.x * a2p.y - a2b.x * a2b.y;
        return x * y >= 0;
    }
    /**
       Find the intersection point of a ray and a plane.
       
       <p>It does not matter which side of the plane the ray origin is on. In
       other words, the ray may be <em>looking at</em> the front or back of
       the plane.</p>
       
       @param pp a point on the plane
       @param pn the plane normal
       @param rp the ray origin
       @param rn the ray direction
       @param out the intersection point
       @return true if the ray intersects the plane
    */
    public static boolean xsectRayPlane(Vec3 pp, Vec3 pn,
                                        Vec3 rp, Vec3 rn,
                                        Vec3 out) {
        pn = pn.norm();
        rn = rn.norm();
        double d = pn.dot(rn);
        if (d != 0) {
            Vec3 pprp = Vec3.sub(pp, rp);
            double t = pprp.dot(pn) / d;
            out.set(Vec3.add(rp, Vec3.mul(rn, t)));
            return t >= 0;
        }
        return false;
    }
    /**
       Get the 2d bounds of the vertices.
       <p>
       Only the x and y components of the Vec3 are queried.
       </p>
       @return a Vec4 whose components are:<pre>
       .x -- minimum x coord
       .y -- minimum y coord
       .z -- maximum x coord
       .w -- maximum y coord
       </pre>
    */
    static public Vec4 bbox2d(Vec3 ... vertices) {
        Vec4 out = new Vec4(Double.POSITIVE_INFINITY,
                            Double.POSITIVE_INFINITY,
                            Double.NEGATIVE_INFINITY,
                            Double.NEGATIVE_INFINITY);
        for (Vec3 v : vertices) {
            if (v.x < out.x) out.x = v.x;
            if (v.y < out.y) out.y = v.y;
            if (v.x > out.z) out.z = v.x;
            if (v.y > out.w) out.w = v.y;
        }
        return out;
    }
    /**
       Get the area of a 2d triangle.
       <p>
       Only the x and y components of the Vec3 are queried.
       </p>
    */
    static public double triArea2d(Vec3 v1, Vec3 v2, Vec3 v3) {
        return ((v1.y - v3.y) * (v2.x - v3.x) +
                (v2.y - v3.y) * (v3.x - v1.x));
    }
}
