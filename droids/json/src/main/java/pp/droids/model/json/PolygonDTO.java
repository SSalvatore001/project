//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Item;
import pp.droids.model.item.Polygon;
import pp.util.ElevatedPoint;
import pp.util.ElevatedSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * DTO class for polygons like mazes.
 */
class PolygonDTO extends ItemDTO {

    /**
     * The name specifying the type of this polygon. It is used for determining the
     * material for rendering the polygon.
     */
    @JsonProperty
    String spec;

    /**
     * The list of coordinates of any polygon corner, both inner and outer border.
     * Each corner has four consecutive values (x, y, bottom, and top).
     */
    @JsonProperty
    float[] points;

    /**
     * The list of indices (pointing to {@linkplain #points}) of outer border corners.
     */
    @JsonProperty
    int[] outer;

    /**
     * An array of arrays of indices, each inner array containing the indices (pointing to {@linkplain #points})
     * of the corners of an inner border.
     */
    @JsonProperty
    int[][] inner;

    /**
     * An array of diagonals. Each diagonal is represented by two consecutive values, the indices
     * (pointing to {@linkplain #points}) of the connected corners.
     */
    @JsonProperty
    int[] diagonals;

    /**
     * Default constructor just for Jackson
     */
    private PolygonDTO() { /* empty */ }

    /**
     * Creates a new polygon DTO of a polygon.
     *
     * @param item a polygon to be represented by the DTO.
     */
    PolygonDTO(Polygon item, Map<Item, String> idMap) {
        super(item, idMap);
        new Initializer(item).initialize();
    }

    /**
     * Auxiliary class for initializing a polygon representation.
     */
    private class Initializer {
        private final Polygon item;
        private final List<ElevatedPoint> allPoints;

        Initializer(Polygon item) {
            this.item = item;
            allPoints = item.getAllSegments().stream().map(ElevatedSegment::to).toList();
        }

        /**
         * Initializes the containing PolygonDTO object.
         */
        void initialize() {
            spec = item.getSpec();
            points = makePointArray();
            outer = makeIndexArray(item.getOuterSegmentList());
            if (!item.getHoleSegmentLists().isEmpty()) {
                inner = new int[item.getHoleSegmentLists().size()][];
                int i = 0;
                for (List<ElevatedSegment> hole : item.getHoleSegmentLists())
                    inner[i++] = makeIndexArray(hole);
            }
            if (!item.getDiagonals().isEmpty())
                diagonals = makeSegmentIndexArray(item.getDiagonals());
        }

        /**
         * getting all Points in form of an Array
         *
         * @return float array
         */
        private float[] makePointArray() {
            final float[] coords = new float[4 * allPoints.size()];
            int i = 0;
            for (ElevatedPoint p : allPoints) {
                coords[i++] = p.x();
                coords[i++] = p.y();
                coords[i++] = p.bottom();
                coords[i++] = p.top();
            }
            return coords;
        }

        /**
         * returns an array of all indizes from all selected Points
         *
         * @param list list of selected Points
         * @return int array of indizes
         */
        private int[] makeIndexArray(List<ElevatedSegment> list) {
            final List<ElevatedPoint> selectedPoints = list.stream().map(ElevatedSegment::to).toList();
            final int[] indices = new int[selectedPoints.size()];
            int i = 0;
            for (ElevatedPoint p : selectedPoints)
                indices[i++] = findPoint(p);
            return indices;
        }

        /**
         * returns index of given Point
         *
         * @param p Point
         * @return index as int
         */
        private int findPoint(ElevatedPoint p) {
            final int index = allPoints.indexOf(p);
            if (index < 0)
                throw new RuntimeException(p + " does not occur in point list");
            return index;
        }

        /**
         * returns an array of all indizes from all selected Segments
         *
         * @param list list of selected Segments
         * @return int array of indizes
         */
        private int[] makeSegmentIndexArray(List<ElevatedSegment> list) {
            final int[] indices = new int[2 * list.size()];
            int i = 0;
            for (ElevatedSegment seg : list) {
                indices[i++] = findPoint(seg.from());
                indices[i++] = findPoint(seg.to());
            }
            return indices;
        }
    }

    /**
     * creates new item in the model
     *
     * @param model the model
     * @return the created item
     */
    @Override
    Item makeItem(DroidsModel model) {
        if (points.length % 4 != 0)
            throw new RuntimeException(format("points must have a multiple of 4 of values, but polygon %s has %d",
                                              id, points.length));
        if (diagonals != null && diagonals.length % 2 != 0)
            throw new RuntimeException(format("diagonals must have an even number of values, but polygon %s has %d",
                                              id, diagonals.length));
        return new Maker(model).makeItem();
    }

    /**
     * Auxiliary class for creating a polygon from its representations.
     */
    private class Maker {
        private final DroidsModel model;
        private final ElevatedPoint[] pointArray;

        Maker(DroidsModel model) {
            this.model = model;
            pointArray = new ElevatedPoint[points.length / 4];
            for (int i = 0; i < pointArray.length; i++)
                pointArray[i] = new ElevatedPoint(points[4 * i], points[4 * i + 1], points[4 * i + 2], points[4 * i + 3]);
        }

        Polygon makeItem() {
            final List<ElevatedPoint> outerPoints = makePointList(outer);
            final List<List<ElevatedPoint>> holeList = new ArrayList<>();
            if (inner != null)
                for (int[] hole : inner)
                    holeList.add(makePointList(hole));
            final List<ElevatedSegment> diags = makeSegmentList(diagonals);
            final Polygon polygon = new Polygon.Builder().setModel(model)
                                                         .setSpec(spec)
                                                         .setOuter(outerPoints)
                                                         .setInner(holeList)
                                                         .setDiagonals(diags)
                                                         .build();
            if (destroyed)
                polygon.destroy();
            return polygon;
        }

        private List<ElevatedPoint> makePointList(int[] indices) {
            final List<ElevatedPoint> list = new ArrayList<>(indices.length);
            for (int i : indices)
                list.add(pointArray[i]);
            return list;
        }

        private List<ElevatedSegment> makeSegmentList(int[] indices) {
            if (indices == null)
                return Collections.emptyList();
            final List<ElevatedSegment> list = new ArrayList<>(indices.length / 2);
            for (int i = 0; i < indices.length; i += 2)
                list.add(new ElevatedSegment(pointArray[indices[i]], pointArray[indices[i + 1]]));
            return list;
        }
    }
}
