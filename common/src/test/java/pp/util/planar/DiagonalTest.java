//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiagonalTest {
    final PlanarMap map = new PlanarMap();
    final Vertex v1 = new Vertex("v1", 0, 0); //NON-NLS
    final Vertex v2 = new Vertex("v2", 0, 5); //NON-NLS
    final Vertex v3 = new Vertex("v3", 5, 7); //NON-NLS
    final Vertex v4 = new Vertex("v4", 10, 5); //NON-NLS
    final Vertex v5 = new Vertex("v5", 10, 1); //NON-NLS
    final Vertex v6 = new Vertex("v6", 1, 3); //NON-NLS
    final Vertex v7 = new Vertex("v7", 2, 3); //NON-NLS
    final Vertex v8 = new Vertex("v8", 1, 4); //NON-NLS
    final Vertex v9 = new Vertex("v9", 8, 2); //NON-NLS
    final Vertex v10 = new Vertex("v10", 9, 2); //NON-NLS
    final Vertex v11 = new Vertex("v11", 9, 3); //NON-NLS
    final Face f1 = map.addPolygon(List.of(v1, v2, v3, v4, v5), map.getOuter(), "f1"); //NON-NLS
    final Face f2 = map.addPolygon(List.of(v6, v7, v8), f1, "f2"); //NON-NLS
    final Face f3 = map.addPolygon(List.of(v9, v10, v11), f1, "f3"); //NON-NLS

    @Test
    public void leftmostTest() {
        assertEquals(v1, f1.leftmostVertex());
        assertEquals(v6, f2.leftmostVertex());
        assertEquals(v9, f3.leftmostVertex());
    }

    @Test
    public void hitTest() {
        final HitInfo hi1 = new Ray(v5, -10, 1).findHit(map, false);
        assertEquals(v2.halfEdgeTo(v1), hi1.halfEdge);
    }

    @Test
    public void diagonalTest() {
        checkHalfEdges(map);

        map.addDiagonal(v4, v7);
        checkCycle(map.getOuter(), v1, v2, v3, v4, v5);
        checkCycle(f1, v1, v5, v4, v7, v6, v8, v7, v4, v3, v2);
        checkCycle(f2, v6, v7, v8);
        checkCycle(f3, v9, v10, v11);
        checkCycle(f1, v11, v10, v9);
        checkHalfEdges(map);
        final List<Face> faces = new ArrayList<>(map.getFaces());

        map.addDiagonal(v1, v4);
        final ArrayList<Face> newFaces = new ArrayList<>(map.getFaces());
        newFaces.removeAll(faces);
        assertEquals(1, newFaces.size());
        final Face newFace = newFaces.get(0);
        checkCycle(map.getOuter(), v1, v2, v3, v4, v5);
        checkCycle(f1, v1, v4, v7, v6, v8, v7, v4, v3, v2);
        checkCycle(f2, v6, v7, v8);
        checkCycle(newFace, v1, v5, v4);
        checkCycle(newFace, v9, v11, v10);
        checkHalfEdges(map);
    }

    private static void checkCycle(Face f, Vertex... vertices) {
        assertEquals(cycle(vertices), vertices[0].halfEdgeTo(vertices[1]).cycle());
        for (var e : cycle(vertices))
            assertEquals(f, e.getIncidentFace());
    }

    @Test
    public void diagonalEndTest() {
        Ray r1 = new Ray(v4, v1);
        HitVertex hv1 = new HitVertex(1, v1);
        final HitInfo hi1 = hv1.createHitInfo(r1);
        assertEquals(v1.halfEdgeTo(v5), hi1.halfEdge);

        Ray r2 = new Ray(v1, v4);
        HitVertex hv2 = new HitVertex(1, v4);
        final HitInfo hi2 = hv2.createHitInfo(r2);
        assertEquals(v4.halfEdgeTo(v3), hi2.halfEdge);
    }

    private static List<HalfEdge> cycle(Vertex... vertices) {
        List<HalfEdge> list = new ArrayList<>();
        for (int i = 0; i < vertices.length; i++)
            list.add(vertices[i].halfEdgeTo(vertices[(i + 1) % vertices.length]));
        return list;
    }

    private static void checkHalfEdges(PlanarMap map) {
        for (HalfEdge e : map.getHalfEdges()) {
            assertNotNull(e.getTwin(), "is null: twin of " + e);
            assertNotNull(e.getIncidentFace(), "is null: incident face of " + e);
            assertNotNull(e.getOrigin(), "is null: origin of " + e);
            assertNotNull(e.getPrev(), "is null: prev of " + e);
            assertNotNull(e.getNext(), "is null: next of " + e);
            assertEquals(e, e.getNext().getPrev(), "not equal: next-prev of " + e);
            assertEquals(e, e.getPrev().getNext(), "not equal: prev-next of " + e);
        }
    }
}