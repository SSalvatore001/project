//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.Position;
import pp.util.SimpleTriangle;
import pp.util.Triangle;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import static pp.util.Triangle.isRightTurn;
import static pp.util.Util.getArea;
import static pp.util.triangulation.Vertex.comparePoints;

/**
 * Represents a polygon with holes to be triangulated by first splitting it into monotone
 * sub-polygons and then triangulating each of these sub-polygons described in chapter 3 of
 * <p>
 * Mark de Berg, Otfried Cheong, Marc van Kreveld, Mark Overmars:
 * Computational Geometry - Algorithms and Applications,
 * 3rd edition, Springer-Verlag Berlin Heidelberg, 2008,
 * DOI: 10.1007/978-3-540-77974-2.
 */
public class Polygon {
    private static final Logger LOGGER = System.getLogger(Polygon.class.getName());

    private final List<Vertex> vertices = new ArrayList<>();
    private final TreeSet<Vertex> tree = new TreeSet<>(Vertex::treeCompare);
    private final Deque<Vertex> stack = new ArrayDeque<>();
    private final List<Triangle> triangles = new ArrayList<>();

    /**
     * Creates a new polygon with the specified sequence of outer points and no holes yet.
     * The orientation of the point list does not matter.
     */
    public Polygon(List<? extends Position> outer) {
        add(outer, false);
    }

    /**
     * Adds a hole to the polygon. It is the caller's responsibility that the hole completely
     * lies within the polygon and that no holes intersect. The orientation of the point
     * list does not matter.
     * <p>
     * Do not call this method after you have called {@linkplain #triangulate()}
     */
    public void addHole(List<? extends Position> inner) {
        if (!triangles.isEmpty())
            throw new IllegalStateException("Don't call Polygon::addHole after Polygon::triangulate");
        add(inner, true);
    }

    private void add(List<? extends Position> pointList, boolean hole) {
        if (pointList.size() < 3)
            throw new IllegalArgumentException("list has just " + pointList.size() + " points");
        final Vertex[] vertex = new Vertex[pointList.size()];
        final int d = (getArea(pointList) >= 0) == hole ? 1 : -1;
        int index = d > 0 ? 0 : vertex.length - 1;
        for (Position p : pointList) {
            final Vertex v = new Vertex(p);
            vertices.add(v);
            vertex[index] = v;
            index += d;
        }
        for (int i = 0; i < vertex.length; i++) {
            final Vertex cur = vertex[i];
            final Vertex next = vertex[(i + 1) % vertex.length];
            cur.next = next;
            next.prev = cur;
            cur.out = new HalfEdge(cur, next);
            if (i > 0) {
                final Vertex prev = vertex[i - 1];
                prev.out.next = cur.out;
                cur.out.prev = prev.out;
            }
            if (next.out != null) {
                cur.out.next = next.out;
                next.out.prev = cur.out;
            }
        }
        for (Vertex v : vertex)
            v.type = VertexType.type(v.prev, v, v.next);
    }

    /**
     * Triangulate this polygon and returns the obtained list of triangles.
     * <p>
     * Do not call {@linkplain #addHole(java.util.List)} after this method has been called.
     *
     * @return the list of triangles triangulating this polygon.
     */
    public List<Triangle> triangulate() {
        // Sort vertices from top to bottom and from left to right
        vertices.sort(Vertex::comparePoints);
        LOGGER.log(Level.DEBUG, "vertices: {0}", vertices); //NON-NLS
        // Sweep line algorithm for splitting the polygon into monotone sub-polygons by adding diagonals.
        vertices.forEach(this::handleVertex);
        // Finally triangulate each of the monotone sub-polygons
        triangulateMonotoneSubPolygons();
        return triangles;
    }

    private void handleVertex(Vertex v) {
        switch (v.type) {
            case END -> handleEndVertex(v);
            case SPLIT -> handleSplitVertex(v);
            case MERGE -> handleMergeVertex(v);
            case START -> handleStartVertex(v);
            case REGULAR -> handleRegularVertex(v);
        }
    }

    private void handleEndVertex(Vertex v) {
        if (v.prev.helper.from.type == VertexType.MERGE)
            addDiagonal(v, v.prev.helper);
        tree.remove(v.prev);
    }

    private void handleSplitVertex(Vertex v1) {
        final Vertex v2 = tree.floor(v1);
        assert v2 != null;
        addDiagonal(v1, v2.helper);
        // v1.out.prev.twin is the half edge of the diagonal
        // that has just been added and that points upward
        v2.helper = v1.out.prev.twin;
        tree.add(v1);
        v1.helper = v1.out;
    }

    private void handleMergeVertex(Vertex v) {
        handleEndVertex(v);
        redirectHelper(v);
    }

    private void handleRegularVertex(Vertex v) {
        if (v.onLeftBorder()) {
            // interior is on the right of v
            handleEndVertex(v);
            handleStartVertex(v);
        }
        else
            redirectHelper(v);
    }

    private void handleStartVertex(Vertex v) {
        tree.add(v);
        v.helper = v.out;
    }

    private void redirectHelper(Vertex v1) {
        final Vertex v2 = tree.floor(v1);
        assert v2 != null;
        if (v2.helper.from.type == VertexType.MERGE) {
            addDiagonal(v1, v2.helper);
            // v1.out.prev.twin is the half edge of the diagonal
            // that has just been added and that points upward
            v2.helper = v1.out.prev.twin;
        }
        else
            v2.helper = v1.out;
    }

    /**
     * Adds a diagonal between the specified vertex and the start vertex of the specified half edge.
     * The specified vertex must be lower than or right of the start vertex of the specified half edge
     * (using {@linkplain Vertex#comparePoints(Vertex, Vertex)}),
     */
    private void addDiagonal(Vertex v, HalfEdge e) {
        assert comparePoints(v, e.from) > 0 : "wrong diagonal " + v + " -> " + e.from;
        LOGGER.log(Level.DEBUG, "add diagonal from {0} to {1}", v, e.from); //NON-NLS
        final HalfEdge up = new HalfEdge(v, e.from);
        final HalfEdge down = new HalfEdge(e.from, v);
        up.twin = down;
        down.twin = up;
        up.prev = v.out.prev;
        up.next = e;
        down.prev = e.prev;
        down.next = v.out;
        up.next.prev = up;
        up.prev.next = up;
        down.next.prev = down;
        down.prev.next = down;
    }

    /**
     * Looks for all monotone sub-polygons and triangulates each.
     */
    private void triangulateMonotoneSubPolygons() {
        final Deque<HalfEdge> todo = new LinkedList<>();
        todo.addLast(vertices.get(0).out);
        while (!todo.isEmpty()) {
            final HalfEdge edge = todo.removeFirst();
            if (!edge.visited) {
                for (HalfEdge e = edge; !e.visited; e = e.next) {
                    e.visited = true;
                    if (e.twin != null && !e.twin.visited)
                        todo.addLast(e.twin);
                }
                triangulate(edge);
            }
        }
    }

    /**
     * Triangulate the monotone sub-polygon that has the specified half-edge on its border.
     *
     * @param e the half-edge specifying the monotone sub-polygon
     */
    private void triangulate(HalfEdge e) {
        final HalfEdge min = topmostVertex(e);
        LOGGER.log(Level.DEBUG, () -> log(min));
        final var it = sortMonotonePolygon(min).iterator();
        stack.clear();
        stack.addLast(it.next());
        stack.addLast(it.next());
        while (it.hasNext()) {
            final Vertex cur = it.next();
            assert !stack.isEmpty();
            if (it.hasNext() && cur.isLeft == stack.peekLast().isLeft)
                sameChains(cur);
            else
                oppositeChains(cur);
        }
    }

    /**
     * Determines the top-most vertex of the monotone sub-polygon that
     * has the specified half-edge on its border.
     *
     * @param edge the half-edge specifying the monotone sub-polygon
     * @return the half-edge that starts at the top-most vertex
     */
    private static HalfEdge topmostVertex(HalfEdge edge) {
        while (comparePoints(edge.next.from, edge.from) < 0)
            edge = edge.next;
        while (comparePoints(edge.prev.from, edge.from) < 0)
            edge = edge.prev;
        return edge;
    }

    /**
     * Returns the sorted list (from top to bottom and then from left to right) of
     * the monotone sub-polygon whose top-most vertex is the start-vertex of the
     * specified half-edge.
     */
    private static List<Vertex> sortMonotonePolygon(HalfEdge min) {
        List<Vertex> edges = new ArrayList<>();
        edges.add(min.from);
        HalfEdge left = min.next;
        HalfEdge right = min.prev;
        while (left.from != right.from) {
            final int c = comparePoints(left.from, right.from);
            assert c != 0;
            if (c < 0) {
                left.from.isLeft = true;
                edges.add(left.from);
                left = left.next;
            }
            else {
                right.from.isLeft = false;
                edges.add(right.from);
                right = right.prev;
            }
        }
        edges.add(left.from);
        return edges;
    }

    /**
     * The specified vertex is in the same chain as the top of the stack.
     */
    private void sameChains(Vertex cur) {
        Vertex v = stack.removeLast();
        if (v.isLeft)
            while (!stack.isEmpty() && isRightTurn(cur, v, stack.peekLast())) {
                addRightTriangle(cur, v, stack.peekLast());
                v = stack.removeLast();
            }
        else
            while (!stack.isEmpty() && isRightTurn(cur, stack.peekLast(), v)) {
                addRightTriangle(cur, stack.peekLast(), v);
                v = stack.removeLast();
            }
        stack.addLast(v);
        stack.addLast(cur);
    }

    /**
     * The specified vertex is not in the same chain as the top of the stack.
     */
    private void oppositeChains(Vertex cur) {
        final Vertex top = stack.removeLast();
        for (Vertex v = top; !stack.isEmpty(); v = stack.removeLast())
            addTriangle(cur, v, stack.peekLast());
        stack.addLast(top);
        stack.addLast(cur);
    }

    private void addTriangle(Vertex p1, Vertex p2, Vertex p3) {
        if (isRightTurn(p1, p2, p3))
            addRightTriangle(p1, p2, p3);
        else
            addRightTriangle(p1, p3, p2);
    }

    private void addRightTriangle(Vertex p1, Vertex p2, Vertex p3) {
        // deliberately use p1.pos, p2.pos, p3.pos in order to refer to the
        // original vertices of the polygon
        final Triangle t = new SimpleTriangle(p1.pos, p2.pos, p3.pos);
        LOGGER.log(Level.DEBUG, "add triangle {0}", t); //NON-NLS
        triangles.add(t);
    }

    private static String log(HalfEdge edge) {
        final StringBuilder b = new StringBuilder("Component: "); //NON-NLS
        HalfEdge e = edge;
        while (true) {
            b.append(e.from);
            e = e.next;
            if (e == edge)
                break;
            b.append(", ");
        }
        return b.toString();
    }
}
