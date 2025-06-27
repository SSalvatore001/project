//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import java.util.List;
import java.util.Locale;

public class PlanarMapDemo {
    private final PlanarMap map = new PlanarMap();
    private int ctr = 0;

    public static void main(String[] args) {
        new PlanarMapDemo().run();
    }

    private void run() {
        final Face f1 = map.addPolygon(List.of(new Vertex("v1", 0, 0), //NON-NLS
                                               new Vertex("v2", 0, 4), //NON-NLS
                                               new Vertex("v3", 5, 4), //NON-NLS
                                               new Vertex("v4", 5, 0)), //NON-NLS
                                       map.getOuter(), "f1"); //NON-NLS
        final Face f2 = map.addPolygon(List.of(new Vertex("v5", 1, 1), //NON-NLS
                                               new Vertex("v6", 1, 2), //NON-NLS
                                               new Vertex("v7", 2, 2), //NON-NLS
                                               new Vertex("v8", 2, 1)), //NON-NLS
                                       f1, "f2"); //NON-NLS
        final Face f3 = map.addPolygon(List.of(new Vertex("v9", 3, 1), //NON-NLS
                                               new Vertex("v10", 3, 2), //NON-NLS
                                               new Vertex("v11", 4, 2), //NON-NLS
                                               new Vertex("v12", 4, 1)), //NON-NLS
                                       f1, "f3"); //NON-NLS
        final Face f4 = map.addPolygon(List.of(new Vertex("v13", 2, 3), //NON-NLS
                                               new Vertex("v14", 2.5f, 3.5f), //NON-NLS
                                               new Vertex("v15", 3, 3), //NON-NLS
                                               new Vertex("v16", 2.5f, 2.5f)), //NON-NLS
                                       f1, "f4"); //NON-NLS
        //System.out.println(map.detail());

        hit(-1, 0, -1, 0);
        hit(0.5f, 0.5f, 1, 0);
        hit(-1, 0, 1, 0);
        hit(-1, 2, 1, 0);
        hit(0.5f, 0.5f, 0.333333333333f, 1f / 3f);
        hit(1.5f, 1.5f, 0.333333333333f, 1f / 3f);
        hit(1.5f, 0.5f, -1.1f, 1);
        hit(1.5f, 0.5f, 1.1f, 1);
        hit(1.5f, 0.5f, -1, 1);
        hit(1.5f, 1.5f, -1, -1);
        hit(1.5f, 1.5f, -1, 1);
        hit(1.5f, 1.5f, 1, 1);
        hit(1.5f, 1.5f, 1, -1);
        hit(2.5f, 1.5f, -1, 1);
        hit(2.5f, 2, -1, 0);
        hit(2.4f, 2.4f, -1, -1);
        hit(1.5f, 2.5f, 1, -1);
        hit(2.5f, 2.5f, -1, -1);
        hit(2, 0.5f, 0.45f, 1);
        hit(2, 0.5f, 0.4f, 1);
        hit(2, 0.5f, 0.35f, 1);
        hit(2, 0, 1, 0);
        hit(2, 0, -1, 0);
        hit(2, 0, 0, 1);
        hit(2, 0, 0, -1);
    }

    private void hit0(float x, float y, float dx, float dy) {
        System.out.println(new Ray(x, y, dx, dy).findHit(map));
    }

    private void hit(float x, float y, float dx, float dy) {
        //System.out.println(map.findHit(new Ray(x, y, dx, dy)));
        createTestCase(x, y, dx, dy);
    }

    private void createTestCase(float x, float y, float dx, float dy) {
        StringBuilder b = new StringBuilder();
        final HitInfo hit = new Ray(x, y, dx, dy).findHit(map);
        System.out.println("@Test"); //NON-NLS
        System.out.println("public void testHit" + ++ctr + "() {"); //NON-NLS
        System.out.printf(Locale.US, "final Ray ray = new Ray(%s, %s, %s, %s);%n", x, y, dx, dy); //NON-NLS
        System.out.println("final HitInfo hit = map.findHit(ray);"); //NON-NLS
        System.out.println("assertEquals(ray, hit.ray);"); //NON-NLS
        if (hit.halfEdge == null)
            System.out.println("assertNull(hit.halfEdge);"); //NON-NLS
        else
            System.out.printf("assertEquals(halfEdge(%s, %s), hit.halfEdge);%n", //NON-NLS
                              hit.halfEdge.getOrigin(), hit.halfEdge.getTarget());
        if (hit.face == null) //NON-NLS
            System.out.println("assertNull(hit.face);"); //NON-NLS
        else if (hit.face == map.getOuter())
            System.out.println("assertEquals(map.outer, hit.face);"); //NON-NLS
        else
            System.out.printf("assertEquals(%s, hit.face);%n", hit.face); //NON-NLS
        System.out.printf(Locale.US, "assertEquals(%s, hit.mu, EPS);%n", hit.mu); //NON-NLS
        System.out.printf(Locale.US, "assertEquals(%s, hit.lambda, EPS);%n", hit.lambda); //NON-NLS
        System.out.println("}");
    }
}
