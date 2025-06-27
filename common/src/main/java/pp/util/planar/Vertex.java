//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.Position;

import java.util.Locale;

public class Vertex extends Item implements Position {
    private final float x;
    private final float y;
    private HalfEdge incidentEdge;

    public Vertex(String id, Position p) {
        this(id, p.getX(), p.getY());
    }

    public Vertex(String id, float x, float y) {
        super(id);
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public HalfEdge getIncidentEdge() {
        return incidentEdge;
    }

    public void setIncidentEdge(HalfEdge incidentEdge) {
        this.incidentEdge = incidentEdge;
    }

    /**
     * Looks for a half edge from this vertex to the specified vertex.
     *
     * @param other a vertex
     * @return a half edge from this vertex to the specified vertex, or null if there is no such half edge.
     */
    public HalfEdge halfEdgeTo(Vertex other) {
        HalfEdge e = incidentEdge;
        do {
            final HalfEdge twin = e == null ? null : e.getTwin();
            if (twin == null)
                return null;
            if (twin.getOrigin() == other)
                return e;
            e = twin.getNext();
        }
        while (e != incidentEdge);
        return null;
    }

    @Override
    public String detail() {
        return String.format(Locale.US, "%s[x:%s, y:%s, incident:%s]", //NON-NLS
                             getId(), x, y, incidentEdge);
    }
}
