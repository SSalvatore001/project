//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlanarMapTest {
    private static final float EPS = 1e-5f;

    private final PlanarMap map = new PlanarMap();
    private final Vertex v1 = new Vertex("v1", 0, 0); //NON-NLS
    private final Vertex v2 = new Vertex("v2", 0, 4); //NON-NLS
    private final Vertex v3 = new Vertex("v3", 5, 4); //NON-NLS
    private final Vertex v4 = new Vertex("v4", 5, 0); //NON-NLS
    private final Vertex v5 = new Vertex("v5", 1, 1); //NON-NLS
    private final Vertex v6 = new Vertex("v6", 1, 2); //NON-NLS
    private final Vertex v7 = new Vertex("v7", 2, 2); //NON-NLS
    private final Vertex v8 = new Vertex("v8", 2, 1); //NON-NLS
    private final Vertex v9 = new Vertex("v9", 3, 1); //NON-NLS
    private final Vertex v10 = new Vertex("v10", 3, 2); //NON-NLS
    private final Vertex v11 = new Vertex("v11", 4, 2); //NON-NLS
    private final Vertex v12 = new Vertex("v12", 4, 1); //NON-NLS
    private final Vertex v13 = new Vertex("v13", 2, 3); //NON-NLS
    private final Vertex v14 = new Vertex("v14", 2.5f, 3.5f); //NON-NLS
    private final Vertex v15 = new Vertex("v15", 3, 3); //NON-NLS
    private final Vertex v16 = new Vertex("v16", 2.5f, 2.5f); //NON-NLS
    private final Face f1 = map.addPolygon(List.of(v1, v2, v3, v4), map.getOuter(), "f1"); //NON-NLS
    private final Face f2 = map.addPolygon(List.of(v5, v8, v7, v6), f1, "f2"); //NON-NLS
    private final Face f3 = map.addPolygon(List.of(v9, v10, v11, v12), f1, "f3"); //NON-NLS
    private final Face f4 = map.addPolygon(List.of(v13, v16, v15, v14), f1, "f4"); //NON-NLS

    private static HalfEdge halfEdge(Vertex src, Vertex tgt) {
        final HalfEdge e = src.halfEdgeTo(tgt);
        assertNotNull(e, "no half edge from " + src + " to " + tgt);
        return e;
    }

    @Test
    public void testHit1() {
        final Ray ray = new Ray(-1.0f, 0.0f, -1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertNull(hit.halfEdge);
        assertEquals(map.getOuter(), hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit2() {
        final Ray ray = new Ray(0.5f, 0.5f, 1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v4, v3), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(4.5f, hit.mu, EPS);
        assertEquals(0.125, hit.lambda, EPS);
    }

    @Test
    public void testHit3() {
        final Ray ray = new Ray(-1.0f, 0.0f, 1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v1, v2), hit.halfEdge);
        assertEquals(map.getOuter(), hit.face);
        assertEquals(1.0f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit4() {
        final Ray ray = new Ray(-1.0f, 2.0f, 1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v1, v2), hit.halfEdge);
        assertEquals(map.getOuter(), hit.face);
        assertEquals(1.0f, hit.mu, EPS);
        assertEquals(0.5f, hit.lambda, EPS);
    }

    @Test
    public void testHit5() {
        final Ray ray = new Ray(0.5f, 0.5f, 0.333333333333f, 0.3333333333333333f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v5, v6), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(1.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit6() {
        final Ray ray = new Ray(1.5f, 1.5f, 0.333333333333f, 0.3333333333333333f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v6), hit.halfEdge);
        assertEquals(f2, hit.face);
        assertEquals(1.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit7() {
        final Ray ray = new Ray(1.5f, 0.5f, -1.1f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v2, v1), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(1.3636363636363635, hit.mu, EPS);
        assertEquals(0.5340909090909092, hit.lambda, EPS);
    }

    @Test
    public void testHit8() {
        final Ray ray = new Ray(1.5f, 0.5f, 1.1f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v9, v10), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(1.3636363636363635, hit.mu, EPS);
        assertEquals(0.8636363636363635, hit.lambda, EPS);
    }

    @Test
    public void testHit9() {
        final Ray ray = new Ray(1.5f, 0.5f, -1.0f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v5, v6), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit10() {
        final Ray ray = new Ray(1.5f, 1.5f, -1.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v5, v8), hit.halfEdge);
        assertEquals(f2, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit11() {
        final Ray ray = new Ray(1.5f, 1.5f, -1.0f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v6, v5), hit.halfEdge);
        assertEquals(f2, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit12() {
        final Ray ray = new Ray(1.5f, 1.5f, 1.0f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v6), hit.halfEdge);
        assertEquals(f2, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit13() {
        final Ray ray = new Ray(1.5f, 1.5f, 1.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v8, v7), hit.halfEdge);
        assertEquals(f2, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit14() {
        final Ray ray = new Ray(2.5f, 1.5f, -1.0f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v8), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit15() {
        final Ray ray = new Ray(2.5f, 2.0f, -1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v8), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit16() {
        final Ray ray = new Ray(2.4f, 2.4f, -1.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v8), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.4, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit17() {
        final Ray ray = new Ray(1.5f, 2.5f, 1.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v7, v8), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit18() {
        final Ray ray = new Ray(2.5f, 2.5f, -1.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v15, v16), hit.halfEdge);
        assertNull(hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit19() {
        final Ray ray = new Ray(2.0f, 0.5f, 0.45f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v3, v2), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(3.5f, hit.mu, EPS);
        assertEquals(0.285, hit.lambda, EPS);
    }

    @Test
    public void testHit20() {
        final Ray ray = new Ray(2.0f, 0.5f, 0.4f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v15, v16), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(2.5f, hit.mu, EPS);
        assertEquals(0.0f, hit.lambda, EPS);
    }

    @Test
    public void testHit21() {
        final Ray ray = new Ray(2.0f, 0.5f, 0.35f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v15, v16), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(2.3076923076923075, hit.mu, EPS);
        assertEquals(0.3846153846153848, hit.lambda, EPS);
    }

    @Test
    public void testHit22() {
        final Ray ray = new Ray(2.0f, 0.0f, 1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v1, v4), hit.halfEdge);
        assertNull(hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.4, hit.lambda, EPS);
    }

    @Test
    public void testHit23() {
        final Ray ray = new Ray(2.0f, 0.0f, -1.0f, 0.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v4, v1), hit.halfEdge);
        assertNull(hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.6, hit.lambda, EPS);
    }

    @Test
    public void testHit24() {
        final Ray ray = new Ray(2.0f, 0.0f, 0.0f, 1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v4, v1), hit.halfEdge);
        assertEquals(map.getOuter(), hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.6, hit.lambda, EPS);
    }

    @Test
    public void testHit25() {
        final Ray ray = new Ray(2.0f, 0.0f, 0.0f, -1.0f);
        final HitInfo hit = ray.findHit(map);
        assertEquals(ray, hit.ray);
        assertEquals(halfEdge(v1, v4), hit.halfEdge);
        assertEquals(f1, hit.face);
        assertEquals(0.0f, hit.mu, EPS);
        assertEquals(0.4, hit.lambda, EPS);
    }
}
