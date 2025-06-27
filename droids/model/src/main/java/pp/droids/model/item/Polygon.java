//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;
import pp.util.ElevatedPoint;
import pp.util.ElevatedSegment;
import pp.util.ElevatedTriangle;
import pp.util.Position;
import pp.util.Triangle;
import pp.util.planar.Face;
import pp.util.planar.HalfEdge;
import pp.util.planar.PlanarMap;
import pp.util.planar.Vertex;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * An item with a polygonal floor plan.
 */
public class Polygon extends AbstractItem implements PolygonItem {
    /**
     * The Builder class is used to construct instances of the Polygon class.
     * It provides methods to set the properties of the polygon and build the polygon object.
     */
    public static class Builder {
        private DroidsModel model;
        private String spec;
        private List<ElevatedPoint> outer;
        private List<List<ElevatedPoint>> inner = Collections.emptyList();
        private List<ElevatedSegment> diagonals = Collections.emptyList();

        /**
         * Sets the model of the polygon.
         *
         * @param model the DroidsModel to set
         * @return the Builder object
         */
        public Builder setModel(DroidsModel model) {
            this.model = model;
            return this;
        }

        /**
         * Sets the specification of the polygon.
         *
         * @param spec the specification for the droid
         * @return the Builder object with the updated specification
         */
        public Builder setSpec(String spec) {
            this.spec = spec;
            return this;
        }

        /**
         * Sets the outer list of {@link ElevatedPoint} objects.
         *
         * @param outer the list of {@link ElevatedPoint} objects to set
         * @return the {@link Builder} object with the updated outer list
         */
        public Builder setOuter(List<ElevatedPoint> outer) {
            this.outer = outer;
            return this;
        }

        /**
         * Sets the inner list of ElevatedPoints.
         *
         * @param inner the inner list of ElevatedPoints
         * @return the Builder instance
         */
        public Builder setInner(List<List<ElevatedPoint>> inner) {
            this.inner = inner;
            return this;
        }

        /**
         * Sets the diagonals of the builder object.
         *
         * @param diagonals The list of ElevatedSegments representing the diagonals.
         * @return The updated Builder object.
         */
        public Builder setDiagonals(List<ElevatedSegment> diagonals) {
            this.diagonals = diagonals;
            return this;
        }

        /**
         * Builds a Polygon using the provided parameters.
         *
         * @return A new Polygon instance.
         */
        public Polygon build() {
            return new Polygon(requireNonNull(model), requireNonNull(spec), requireNonNull(outer), inner, diagonals);
        }
    }

    /**
     * The default height of a maze.
     */
    public static final float HEIGHT = 2.5f;
    private static final float EPS = 1e-6f;

    private final String spec;
    private final List<ElevatedSegment> outerSegments;
    private final List<List<ElevatedSegment>> holeSegments = new ArrayList<>();
    private final List<ElevatedSegment> allSegments = new ArrayList<>();
    private final List<ElevatedTriangle> triangles = new ArrayList<>();
    private final List<ElevatedSegment> diagonals;

    private Polygon(DroidsModel model, String spec,
                    List<ElevatedPoint> outer, List<List<ElevatedPoint>> inner,
                    List<ElevatedSegment> diagonals) {
        super(model);
        this.spec = spec;
        this.diagonals = diagonals;
        final Map<Vertex, ElevatedPoint> v2p = new HashMap<>(); // maps vertices to corresponding ElevatedPoints
        final Map<ElevatedPoint, Vertex> p2v = new HashMap<>(); // maps ElevatedPoints to their corresponding vertices
        // create a planar map specified by the outer segments and the holes
        final PlanarMap map = new PlanarMap();
        final Face face = map.addPolygon(vertexList(outer, "o", v2p, p2v), map.getOuter(), "poly"); //NON-NLS
        for (var hole : inner) {
            final String prefix = "h" + face.getInner().size() + "/";
            final String faceId = "hole" + face.getInner().size(); //NON-NLS
            map.addPolygon(vertexList(hole, prefix, v2p, p2v), face, faceId);
        }
        final Set<Face> nonPolyFaces = new LinkedHashSet<>(map.getFaces());
        nonPolyFaces.remove(face);
        // nonPolyFaces now contains the outer face and all holes
        // add all diagonals
        for (ElevatedSegment diagonal : diagonals)
            map.addDiagonal(requireNonNull(p2v.get(diagonal.from())),
                            requireNonNull(p2v.get(diagonal.to())));
        // triangulate each face that is neither the outer face nor a hole
        for (Face f : map.getFaces())
            if (!nonPolyFaces.contains(f)) {
                final var polygon = new pp.util.triangulation.Polygon(pointList(f.getOuter()));
                for (HalfEdge hole : f.getInner())
                    polygon.addHole(pointList(hole));
                for (Triangle t : polygon.triangulate()) {
                    final ElevatedPoint a = requireNonNull(v2p.get(t.a()));
                    final ElevatedPoint b = requireNonNull(v2p.get(t.b()));
                    final ElevatedPoint c = requireNonNull(v2p.get(t.c()));
                    triangles.add(new ElevatedTriangle(a, b, c));
                }
            }

        assert map.getOuter().getInner().size() == 1;
        outerSegments = segmentList(map.getOuter().getInner().get(0), v2p);
        for (Face f : nonPolyFaces)
            if (f != map.getOuter())
                holeSegments.add(segmentList(f.getOuter(), v2p));
        allSegments.addAll(outerSegments);
        holeSegments.forEach(allSegments::addAll);
    }

    private static List<Vertex> vertexList(List<ElevatedPoint> points, String prefix,
                                           Map<Vertex, ElevatedPoint> v2p, Map<ElevatedPoint, Vertex> p2v) {
        final List<Vertex> list = new ArrayList<>(points.size());
        for (ElevatedPoint p : points) {
            final Vertex v = new Vertex(prefix + (list.size() + 1), p);
            v2p.put(v, p);
            p2v.put(p, v);
            list.add(v);
        }
        return list;
    }

    private static List<Position> pointList(HalfEdge e) {
        List<Position> list = new ArrayList<>();
        for (var it = e.cycleIterator(); it.hasNext(); )
            list.add(requireNonNull(it.next().getOrigin()));
        return list;
    }

    @Override
    public void setPos(float x, float y) {
        throw new UnsupportedOperationException("Cannot move a polygon");
    }

    @Override
    public void setRotation(float rotation) {
        throw new UnsupportedOperationException("Cannot turn a polygon");
    }

    public String getSpec() {
        return spec;
    }

    /**
     * Returns the outer segments of the polygon.
     */
    public List<ElevatedSegment> getOuterSegmentList() {
        return outerSegments;
    }

    /**
     * Returns the list of inner segment lists of the polygon.
     */
    public List<List<ElevatedSegment>> getHoleSegmentLists() {
        return holeSegments;
    }

    public List<ElevatedSegment> getDiagonals() {
        return diagonals;
    }

    /**
     * Returns the list of all segments that are bounding the polygon, either to the outside or the inside.
     */
    @Override
    public List<ElevatedSegment> getAllSegments() {
        return allSegments;
    }

    private static List<ElevatedSegment> segmentList(HalfEdge e, Map<Vertex, ElevatedPoint> v2p) {
        List<ElevatedSegment> segList = new ArrayList<>();
        HalfEdge cur = e;
        do {
            segList.add(new ElevatedSegment(requireNonNull(v2p.get(cur.getTarget())),
                                            requireNonNull(v2p.get(cur.getOrigin()))));
            cur = cur.getPrev();
        }
        while (cur != e);
        return segList;
    }

    /**
     * Returns the triangle that contains the specified position,
     * or null if there is no such triangle.
     */
    public ElevatedTriangle findTriangle(float x, float y) {
        for (var t : getTriangles())
            if (t.contains(x, y, EPS))
                return t;
        return null;
    }

    /**
     * Returns the list of all triangles of this polygon after triangulation.
     */
    public List<ElevatedTriangle> getTriangles() {
        return triangles;
    }

    /**
     * Returns the elevation of the specified item by computing the elevation of the
     * corresponding point of the polygon, or 0 if the item is not positioned
     * within the polygon.
     */
    public float getElevation(Item item) {
        final ElevatedTriangle t = item.getTriangle();
        if (t == null)
            return 0f;
        final float elevation = t.top(item);
        LOGGER.log(Level.DEBUG, "elevation of {0} in {1}: {2}", item, t, elevation); //NON-NLS
        return elevation;
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link pp.droids.model.item.VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}
