//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.FloatPoint;
import pp.util.Position;

import static pp.util.FloatMath.abs;
import static pp.util.FloatMath.sqrt;
import static pp.util.SegmentLike.length;
import static pp.util.Triangle.isRightTurn;

/**
 * A vertex of the doubly-connected edge list.
 */
class Vertex implements Position {
    private static final float EPS = 1e-6f;

    /**
     * Coordinates of this vertex in the plane.
     */
    final Position pos;
    /**
     * The type of this vertex.
     */
    VertexType type;
    /**
     * The vertex where the incoming segment of this vertex starts
     */
    Vertex prev;
    /**
     * The vertex where the outgoing segment of this vertex ends.
     */
    Vertex next;
    /**
     * Half-edge starting at the actual helper vertex according to de Berg et al.
     * If a diagonal must be added leaving the actual helper vertex, it must
     * be inserted immediately before this helper half-edge in the list of all half-edges
     * leaving the actual helper vertex.
     */
    HalfEdge helper;
    /**
     * Half-edge of the outgoing segment of this vertex.
     */
    HalfEdge out;
    /**
     * Indicates whether this vertex is on the left chain of vertices of a monotone
     * sub-polygon when triangulating this sub-polygon
     */
    boolean isLeft;

    public Vertex(Position pos) {
        this.pos = pos;
    }

    @Override
    public float getX() {
        return pos.getX();
    }

    @Override
    public float getY() {
        return pos.getY();
    }

    @Override
    public String toString() {
        return "(" + getX() + "|" + getY() + ")" + (type == null ? "" : ":" + type);
    }

    /**
     * Computes the corresponding corner of a virtual wall inside the
     * polygon, i.e., to the left of the border, with distance 1
     * from the polygon border.
     */
    Position corner() {
        final float x1 = prev.getX();
        final float y1 = prev.getY();
        final float x2 = getX();
        final float y2 = getY();
        final float x3 = next.getX();
        final float y3 = next.getY();
        final float dx1 = x2 - x1;
        final float dy1 = y2 - y1;
        final float dx2 = x2 - x3;
        final float dy2 = y2 - y3;
        final float len1 = sqrt(dx1 * dx1 + dy1 * dy1);
        final float len2 = sqrt(dx2 * dx2 + dy2 * dy2);
        final float dx = dx1 / len1 + dx2 / len2;
        final float dy = dy1 / len1 + dy2 / len2;
        final float prod = dy1 * dx - dx1 * dy;
        if (abs(prod) < EPS)
            return new FloatPoint(x2 + dy1 / len1, y2 - dx1 / len1);
        final float f = len1 / prod;
        return new FloatPoint(x2 + f * dx, y2 + f * dy);
    }

    /**
     * Compares vertices based on their position in the plane from top to bottom
     * (y coordinates grow downwards) and then from left ro right.
     */
    static int comparePoints(Vertex v1, Vertex v2) {
        if (v1 == v2) return 0;
        final int c = v1.compareTo(v2);
        if (c != 0) return c;
        // if vertices have the same positions, consider a
        // virtual wall between them
        return v1.corner().compareTo(v2.corner());
    }

    /**
     * Compare function only used in tree as a status structure of all
     * edges currently intersected by the sweep line from left to right.
     * A vertex represents the edge from this vertex to the next one.
     * That way, one can compare edges and vertices on the sweep line.
     */
    static int treeCompare(Vertex v1, Vertex v2) {
        if (v1 == v2) return 0;
        final int cmpY = Float.compare(v1.getY(), v2.getY());
        if (cmpY < 0) {
            // v1 indicates an edge, v2 is a vertex
            return isRightTurn(v1, v2, v1.next) ? -1 : 1;
        }
        if (cmpY > 0) {
            // v2 indicates an edge, v1 is a vertex
            return isRightTurn(v2, v2.next, v1) ? -1 : 1;
        }
        // we must compare x-coordinates
        final int cmpX = Float.compare(v1.getX(), v2.getX());
        if (cmpX != 0) return cmpX;
        // v1 and v2 have the same position. We must check which of the outgoing
        // segments is the left and which the right one
        assert v1.dx() != v2.dx();
        return Float.compare(v1.dx(), v2.dx());
    }

    private float dx() {
        return (next.getX() - getX()) / length(this, next);
    }

    boolean onLeftBorder() {
        return compareTo(prev) >= 0;
    }
}
