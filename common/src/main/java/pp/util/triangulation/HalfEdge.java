//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

/**
 * A half-edge of the doubly-connected edge list.
 */
class HalfEdge {
    /**
     * Start vertex of the half-edge
     */
    final Vertex from;
    /**
     * End vertex of the half-edge
     */
    final Vertex to;
    /**
     * The twin half-edge with the opposite direction. Segments of the original polygon
     * are represented by half-edges in the direction of the segments, but without twins.
     */
    HalfEdge twin;
    /**
     * The previous half-edge in the doubly-linked list of half-edges around the face to the left of this half-edge.
     */
    HalfEdge prev;
    /**
     * The next half-edge in the doubly-linked list of half-edges around the face to the left of this half-edge.
     */
    HalfEdge next;
    /**
     * Flag indicating that this half-edge has already been visited when finding all faces.
     */
    boolean visited;

    HalfEdge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "half edge " + from + " --> " + to; //NON-NLS
    }
}
