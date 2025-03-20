/*
  ObjModel.java
  S. Edward Dolan
  Monday, November 20 2023
*/

package edgrind.geom;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
//
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//
import edgrind.error.ZeroError;

/**
   A Wavefront .obj file reader and model.
 */
public class ObjModel implements Iterable<Tri3> {
    List<Vec3> verts;
    List<Vec3> norms;
    List<Vec2> texts;
    List<Integer> vidxs;
    List<Integer> nidxs;
    List<Integer> tidxs;
    public List<Tri3> tris;
    public AABBox bbox;
    private ObjModel() {
        verts = new ArrayList<Vec3>();
        norms = new ArrayList<Vec3>();
        texts = new ArrayList<Vec2>();
        vidxs = new ArrayList<Integer>();
        nidxs = new ArrayList<Integer>();
        tidxs = new ArrayList<Integer>();
        tris = new ArrayList<Tri3>();
        bbox = new AABBox();
    }
    public AABBox getBBox() {
        return bbox;
    }
    public AABBox getBBox(Mat4 m) {
        List<Vec3> vs = new ArrayList<Vec3>();
        for (Vec3 v : verts)
            vs.add(m.mul(v));
        return AABBox.fromVertices(vs);
    }
    public void printStats() {
        System.out.format("n verts: %d\nn norms: %d\nn texts: %d\n",
                          verts.size(), norms.size(), texts.size());
    }
    @Override
    public Iterator<Tri3> iterator() {
        return new Iterator<Tri3>() {
            private int nextIdx = 0;
            @Override
            public boolean hasNext() {
                return nextIdx < vidxs.size();
            }
            @Override
            public Tri3 next() {
                Vec3 v1 = verts.get(vidxs.get(nextIdx));
                Vec3 n1 = norms.get(nidxs.get(nextIdx++));
                Vec3 v2 = verts.get(vidxs.get(nextIdx));
                Vec3 n2 = norms.get(nidxs.get(nextIdx++));
                Vec3 v3 = verts.get(vidxs.get(nextIdx));
                Vec3 n3 = norms.get(nidxs.get(nextIdx++));
                return new Tri3(v1, v2, v3, n1, n2, n3);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("ObjModel iterator" +
                                                        " is immutable");
            }
        };
    }
    public static ObjModel read(String fileName) {
        return read(fileName, false);
    }
    /**
       Read a .obj file, building an ObjModel.

       <p> Only vertex, normal, and face data are read.  File format. [x]
       means the component is optional.
       <ul>
       <li># ... -- comment</li>
       <li>v x y z [w] -- vertex</li>
       <li>vn i j k -- vertex normal</li>
       <li>f f/n/t f/n/t f/n/t ... -- polygon face</li>
       </ul>
       </p>
    */
    public static ObjModel read(String fileName, boolean reverseWinding) {
        try {
            File f = new File(fileName);
            BufferedReader r = new BufferedReader(new FileReader(f));
            String in;
            ObjModel model = new ObjModel();
            String[] a;
            // v, vn, vt, f
            while ((in = r.readLine()) != null) {
                if (in.startsWith("#") || // comment
                    in.isEmpty() ||       // empty string
                    in.matches("^\\s+$")) // whitespace only
                    continue;
                a = in.split("\\s+");
                // edgrind.Util.printArray(a);
                // v x y z [w]
                if (a[0].equals("v")) {
                    if (reverseWinding)
                        model.verts.add(new Vec3(Double.parseDouble(a[1]),
                                                 Double.parseDouble(a[3]),
                                                 Double.parseDouble(a[2])));
                    else
                        model.verts.add(new Vec3(Double.parseDouble(a[1]),
                                                 Double.parseDouble(a[2]),
                                                 Double.parseDouble(a[3])));
                }
                // vn i j k
                else if (a[0].equals("vn"))
                    model.norms.add(new Vec3(Double.parseDouble(a[1]),
                                             Double.parseDouble(a[2]),
                                             Double.parseDouble(a[3]))
                                    .norm());
                // vt s t
                else if (a[0].equals("vt"))
                    model.texts.add(new Vec2(Double.parseDouble(a[1]),
                                             Double.parseDouble(a[2])));
                // f v/t/n v/t/n v/t/n
                else if (a[0].equals("f")) {
                    for (int i=1; i<a.length; ++i) {
                        String[] idxs = a[i].split("/");
                        model.vidxs.add(new Integer(Integer
                                                    .parseInt(idxs[0]) - 1));
                        model.tidxs.add(new Integer(Integer
                                                    .parseInt(idxs[1]) - 1));
                        model.nidxs.add(new Integer(Integer
                                                    .parseInt(idxs[2]) - 1));
                    }
                }
            }
            model.bbox = AABBox.fromVertices(model.verts);
            for (Tri3 t : model)
                model.tris.add(t);
            return model;
        }
        catch (IOException e) {
            System.out.println("failed to open " + fileName);
            return null;
        }
    }
}
