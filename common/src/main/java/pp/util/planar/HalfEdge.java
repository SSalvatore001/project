//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.FloatMath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class HalfEdge extends Item {
    private HalfEdge twin;
    private HalfEdge prev;
    private HalfEdge next;
    private final Vertex origin;
    private Face incidentFace;

    public HalfEdge(String id, Vertex origin) {
        super(id);
        this.origin = origin;
    }

    public HalfEdge getTwin() {
        return twin;
    }

    public HalfEdge getPrev() {
        return prev;
    }

    public HalfEdge getNext() {
        return next;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public Vertex getTarget() {
        if (twin == null)
            throw new InvalidMapException("twin of " + this + " is missing");
        return twin.getOrigin();
    }

    public void setTwin(HalfEdge other) {
        if (twin != null) {
            twin.twin = null;
            twin = null;
        }
        if (other != null) {
            twin = other;
            twin.twin = this;
        }
    }

    public void setPrev(HalfEdge other) {
        if (prev != null) {
            prev.next = null;
            prev = null;
        }
        if (other != null) {
            prev = other;
            prev.next = this;
        }
    }

    public void setNext(HalfEdge other) {
        if (next != null) {
            next.prev = null;
            next = null;
        }
        if (other != null) {
            next = other;
            next.prev = this;
        }
    }

    public void setIncidentFace(Face incidentFace) {
        this.incidentFace = incidentFace;
    }

    public Face getIncidentFace() {
        return incidentFace;
    }

    public boolean isLeft(float x, float y) {
        final float dx1 = getDx();
        final float dy1 = getDy();
        final float dx0 = x - getOrigin().getX();
        final float dy0 = y - getOrigin().getY();
        return dx1 * dy0 - dy1 * dx0 > 0;
    }

    @Override
    public String detail() {
        return String.format("%s[origin:%s, twin:%s, prev:%s, next:%s, incident:%s]", //NON-NLS
                             getId(), origin, twin, prev, next, incidentFace);
    }

    public float getX1() {
        return origin.getX();
    }

    public float getX2() {
        return getTarget().getX();
    }

    public float getY1() {
        return origin.getY();
    }

    public float getY2() {
        return getTarget().getY();
    }

    public float getDx() {
        return getTarget().getX() - origin.getX();
    }

    public float getDy() {
        return getTarget().getY() - origin.getY();
    }

    public float length() {
        final float dx = getDx();
        final float dy = getDy();
        return FloatMath.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns an iterator that iterates over the cycle of half edges
     * starting at this half edge. The first half edge returned is this
     * half edge.
     *
     * @return iterator of half edges in the cycle.
     */
    public Iterator<HalfEdge> cycleIterator() {
        return new Iterator<>() {
            private HalfEdge cur;

            @Override
            public boolean hasNext() {
                return cur != HalfEdge.this;
            }

            @Override
            public HalfEdge next() {
                if (cur == null) {
                    cur = next;
                    return HalfEdge.this;
                }
                if (cur == HalfEdge.this)
                    throw new NoSuchElementException();
                final HalfEdge result = cur;
                cur = cur.next;
                return result;
            }
        };
    }

    /**
     * Returns the cycle of half edges starting at this half edge.
     */
    public List<HalfEdge> cycle() {
        List<HalfEdge> list = new ArrayList<>();
        for (var it = cycleIterator(); it.hasNext(); )
            list.add(it.next());
        return list;
    }

    /**
     * Returns whether the specified half edge occurs in the cycle of half edges
     * starting at this half edge.
     */
    public boolean cycleContains(HalfEdge other) {
        final var it = cycleIterator();
        while (it.hasNext())
            if (it.next() == other)
                return true;
        return false;
    }

    /**
     * Assigns the specified face as incident face to all half edge occurs
     * in the cycle of half edges starting at this half edge.
     */
    public void cycleAssignIncidentFace(Face face) {
        final var it = cycleIterator();
        while (it.hasNext())
            it.next().setIncidentFace(face);
    }
}
