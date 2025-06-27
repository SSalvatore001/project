//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.FloatRectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Face extends Item {
    private HalfEdge outer;
    private final List<HalfEdge> inner = new ArrayList<>();

    public Face(String id) {
        super(id);
    }

    public List<HalfEdge> getInner() {
        return inner;
    }

    public HalfEdge getOuter() {
        return outer;
    }

    public void setOuter(HalfEdge outer) {
        this.outer = outer;
    }

    @Override
    public String detail() {
        return String.format("%s[outer:%s, inner:%s]", getId(), outer, inner); //NON-NLS
    }

    public FloatRectangle boundingBox() {
        if (outer == null) return null;
        FloatRectangle bounds = new FloatRectangle(outer.getX1(), outer.getY1());
        for (HalfEdge e = outer.getNext(); e != outer; e = e.getNext())
            bounds = bounds.add(e.getX1(), e.getY1());
        return bounds;
    }

    /**
     * Returns the leftmost vertex of this face. If there are several leftmost
     * vertices, the one with the least y-coordinate is returned.
     */
    public Vertex leftmostVertex() {
        Vertex min = null;
        final Iterator<HalfEdge> it = outer.cycleIterator();
        while (it.hasNext()) {
            final HalfEdge e = it.next();
            final Vertex src = e.getOrigin();
            if (min == null || src.getX() < min.getX() || src.getX() == min.getX() && src.getY() < min.getY())
                min = src;
        }
        return min;
    }
}
