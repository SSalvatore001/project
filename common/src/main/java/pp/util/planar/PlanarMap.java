//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.FloatRectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static pp.util.FloatMath.abs;
import static pp.util.Util.getArea;
import static pp.util.Util.reverse;

/**
 * Represents a planar map as a doubly-connected edge list as described in chapter 2 of
 * <p>
 * Mark de Berg, Otfried Cheong, Marc van Kreveld, Mark Overmars:
 * Computational Geometry - Algorithms and Applications,
 * 3rd edition, Springer-Verlag Berlin Heidelberg, 2008,
 * DOI: 10.1007/978-3-540-77974-2.
 */
public class PlanarMap {
    public static final float EPS = 1e-5f;
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<HalfEdge> halfEdges = new ArrayList<>();
    private final Face outer;
    private final List<Face> faces = new ArrayList<>();

    /**
     * Creates a new planar map with an implicitly created outer face.
     */
    public PlanarMap() {
        this(new Face("outer")); //NON-NLS
    }

    /**
     * Creates a new planar map with the specified outer face.
     */
    public PlanarMap(Face outer) {
        this.outer = outer;
        faces.add(outer);
    }

    /**
     * Returns the list of all vertices.
     */
    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    /**
     * Returns the list of all half edges.
     */
    public List<HalfEdge> getHalfEdges() {
        return Collections.unmodifiableList(halfEdges);
    }

    /**
     * Returns the list of all faces.
     */
    public List<Face> getFaces() {
        return Collections.unmodifiableList(faces);
    }

    /**
     * Returns the (infinite) outer face
     */
    public Face getOuter() {
        return outer;
    }

    /**
     * Returns the face that contains the specified point.
     */
    public Face locate(float x, float y) {
        final Ray ray = new Ray(x, y, 1f, 0f);
        final HitInfo hit = ray.findHit(this, true);
        return hit.face;
    }

    /**
     * Creates a polygon with the specified sequence of vertices and adds it as a "hole" in the specified outer face.
     * The sequence of vertices can be either in clockwise or counterclockwise order.
     *
     * @param pVertices the sequence of vertices of the polygon
     * @param outer     the outer face to add the polygon as a hole in
     * @param faceId    the ID of the created inner face
     * @return the inner face created by the polygon
     */
    public Face addPolygon(List<Vertex> pVertices, Face outer, String faceId) {
        validateVertices(pVertices);
        addVertices(pVertices);
        final Face inner = createInnerFace(faceId);
        if (inClockwiseOrder(pVertices))
            addClockwisePolygon(pVertices, outer, inner);
        else
            addClockwisePolygon(reverse(pVertices), outer, inner);
        return inner;
    }

    private void validateVertices(List<Vertex> pVertices) {
        if (pVertices.size() < 3)
            throw new IllegalArgumentException("polygon got only " + pVertices.size() + " vertices");
    }

    /**
     * Checks whether the sequence of vertices is in clockwise order.
     *
     * @param pVertices the sequence of vertices to be checked
     * @return true if the sequence of vertices is in clockwise order, false otherwise
     */
    private static boolean inClockwiseOrder(List<Vertex> pVertices) {
        return getArea(pVertices) <= 0;
    }

    /**
     * Adds the given list of vertices to the existing vertices.
     *
     * @param pVertices the list of vertices to add
     * @return true if the vertices were successfully added, false otherwise
     */
    private boolean addVertices(List<Vertex> pVertices) {
        return vertices.addAll(pVertices);
    }

    /**
     * Creates a new inner face with the specified id and adds it to the list of faces.
     *
     * @param faceId the id of the inner face
     * @return the inner face that was created
     */
    private Face createInnerFace(String faceId) {
        final Face inner = new Face(faceId);
        faces.add(inner);
        return inner;
    }

    /**
     * Essentially the same as {@linkplain #addPolygon(java.util.List, Face, String)},
     * but the sequence of vertices must be in clockwise order
     *
     * @param pVertices the sequence of vertices of the polygon in clockwise order.
     * @param outer     the outer face, which gets inner as a new hole
     * @param inner     the inner face whose outer boundary is the polygon created by this method.
     */
    private void addClockwisePolygon(List<Vertex> pVertices, Face outer, Face inner) {
        final Iterator<Vertex> it = pVertices.iterator();
        Vertex prev = it.next();
        HalfEdge last = null;
        while (it.hasNext()) {
            final Vertex next = it.next();
            final HalfEdge nextEdge = addEdge(prev, next, outer, inner);
            if (last != null) {
                last.setNext(nextEdge);
                last.getTwin().setPrev(nextEdge.getTwin());
            }
            last = nextEdge;
            prev = next;
        }
        final Vertex first = pVertices.get(0);
        final HalfEdge nextEdge = addEdge(prev, first, outer, inner);
        last.setNext(nextEdge);
        nextEdge.getTwin().setNext(last.getTwin());
        nextEdge.setNext(first.getIncidentEdge());
        nextEdge.getTwin().setPrev(first.getIncidentEdge().getTwin());
        inner.setOuter(nextEdge.getTwin());
        outer.getInner().add(nextEdge);
    }

    /**
     * Creates and returns a new half edge to the specified vertex and its twin.
     */
    public static HalfEdge makeEdge(Vertex from, Vertex to) {
        final HalfEdge forward = new HalfEdge(from.getId() + "-" + to.getId(), from);
        final HalfEdge backward = new HalfEdge(to.getId() + "-" + from.getId(), to);
        forward.setTwin(backward);
        return forward;
    }

    /**
     * Adds a half edge from 'from' to 'to' (together with its twin) and the corresponding
     * faces left and right of it (and vice versa for the twin).
     *
     * @param from  start vertex of the half edge (and target vertex of its twin)
     * @param to    target vertex of the half edge (and start vertex of its twin)
     * @param left  left face of the half edge from 'from' to 'to'
     * @param right right face of the half edge from 'from' to 'to'
     * @return the added half edge from 'from' to 'to'
     */
    private HalfEdge addEdge(Vertex from, Vertex to, Face left, Face right) {
        final HalfEdge e = makeEdge(from, to);
        from.setIncidentEdge(e);
        e.setIncidentFace(left);
        e.getTwin().setIncidentFace(right);
        halfEdges.add(e);
        halfEdges.add(e.getTwin());
        return e;
    }

    /**
     * Creates a diagonal between the specified vertices and returns one of the two created half edges.
     *
     * @return the half edge from 'from' to 'to'
     */
    public HalfEdge addDiagonal(Vertex from, Vertex to) {
        final HalfEdge fEdge = makeEdge(from, to);
        final HalfEdge bEdge = fEdge.getTwin();
        final HalfEdge toOut = outgoing(fEdge);
        final HalfEdge fromOut = outgoing(bEdge);
        final Face face = fromOut.getIncidentFace();
        if (toOut.getIncidentFace() != face)
            throw new InvalidMapException("diagonal crosses several faces");
        final HitInfo diagonalHit = new Ray(fEdge).findHit(this, false);
        if (abs(diagonalHit.mu - 1f) > EPS)
            throw new InvalidMapException("diagonal hits another edge first");
        halfEdges.add(fEdge);
        halfEdges.add(bEdge);
        // relink half edge cycles
        final HalfEdge toIn = toOut.getPrev();
        final HalfEdge fromIn = fromOut.getPrev();
        fEdge.setNext(toOut);
        fEdge.setPrev(fromIn);
        bEdge.setNext(fromOut);
        bEdge.setPrev(toIn);
        // set incident faces; this may be preliminary for bEdge
        fEdge.setIncidentFace(face);
        bEdge.setIncidentFace(face);
        // we may have to split fac e
        if (fEdge.cycleContains(bEdge)) {
            // a hole disappears
            final int oldSize = face.getInner().size();
            for (var it = fEdge.cycleIterator(); it.hasNext(); )
                if (face.getInner().remove(it.next()))
                    break;
            if (oldSize == face.getInner().size())
                throw new RuntimeException("hole hasn't been removed");
        }
        else {
            // we have to split fac e
            face.setOuter(fEdge);
            final Face f2 = createInnerFace(face.getId() + "s");
            f2.setOuter(bEdge);
            bEdge.cycleAssignIncidentFace(f2);
            // "move" holes of face to f2 if necessary
            for (HalfEdge e : face.getInner()) {
                final Vertex v = e.getTwin().getIncidentFace().leftmostVertex();
                Ray ray = new Ray(v, -1, 0);
                final HitInfo hit = ray.findHit(this, false);
                if (face != hit.halfEdge.getIncidentFace())
                    f2.getInner().add(e);
            }
            face.getInner().removeAll(f2.getInner());
            for (HalfEdge e : f2.getInner())
                e.cycleAssignIncidentFace(f2);
        }
        return fEdge;
    }

    private static HalfEdge outgoing(HalfEdge diagonal) {
        final Ray ray = new Ray(diagonal);
        final HitInfo hitInfo = new HitVertex(1, diagonal.getTarget()).createHitInfo(ray);
        return hitInfo.halfEdge;
    }

    /**
     * Returns a detailed description of this planar map.
     */
    public String detail() {
        final StringBuilder b = new StringBuilder();
        b.append("Vertices:\n"); //NON-NLS
        for (Vertex v : vertices)
            b.append("   ").append(v.detail()).append('\n');
        b.append("Half edges:\n"); //NON-NLS
        for (HalfEdge e : halfEdges)
            b.append("   ").append(e.detail()).append('\n');
        b.append("Faces:\n"); //NON-NLS
        for (Face f : faces)
            b.append("   ").append(f.detail()).append('\n');
        return b.toString();
    }

    /**
     * Returns the bounding rectangle of this map containing all of its vertices.
     */
    public FloatRectangle boundingBox() {
        if (outer.getInner().isEmpty())
            return null;
        FloatRectangle bounds = null;
        for (HalfEdge start : outer.getInner()) {
            final FloatRectangle b = start.getTwin().getIncidentFace().boundingBox();
            bounds = bounds == null ? b : bounds.add(b);
        }
        return bounds;
    }
}
