//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.tools;

import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.MapType;
import pp.droids.model.Spec;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.droids.model.json.JsonSerializer;
import pp.util.ElevatedPoint;
import pp.util.ElevatedSegment;
import pp.util.FloatPoint;
import pp.util.Position;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import static pp.util.FloatMath.HALF_PI;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * Generator for curved tracks.
 */
public class TrackGenerator {
    private static final Logger LOGGER = System.getLogger(TrackGenerator.class.getName());
    private static final int MAX_SIZE = 5;
    private static final int STEPS = 20;
    private static final float TRACK_WIDTH = 4f;
    private static final float TRACK_HEIGHT = 0.5f;
    private final int size;
    private final List<ElevatedPoint> inner = new ArrayList<>();
    private final List<ElevatedPoint> outer = new ArrayList<>();
    private final List<ElevatedSegment> diagonals = new ArrayList<>();

    /**
     * Main method the TrackGenerator
     *
     * @param args input args(size)
     * @throws IOException saving the generated map got interrupted
     */
    public static void main(String[] args) throws IOException {
        final int n = args.length == 0 ? MAX_SIZE : Integer.parseInt(args[0]);
        for (int size = 1; size <= n; size++)
            generate(size);
    }

    /**
     * creating and saving the track
     *
     * @param size track size
     * @throws IOException saving the generated map got interrupted
     */
    private static void generate(int size) throws IOException {
        final DroidsModel model = new DroidsModel(new JsonSerializer());
        model.setDroidsMap(new TrackGenerator(size).makeMap(model));
        final File file = new File(String.format("maps/track%d.json", size));
        model.saveMap(file);
        LOGGER.log(Level.INFO, "File {0} generated", file); //NON-NLS
    }

    /**
     * Creates a TrackGenerator with the specified track width and its size parameter.
     */
    private TrackGenerator(int size) {
        this.size = size;
    }

    /**
     * generates the track
     *
     * @param model DroidsModel
     * @return generated DroidsMap
     */
    private DroidsMap makeMap(DroidsModel model) {
        final DroidsMap map = new DroidsMap(MapType.CASTLE);
        makeLower();
        makeUpper();
        makeDiagonals();
        final Polygon platform = new Polygon.Builder().setModel(model)
                                                      .setSpec(Spec.STONE)
                                                      .setOuter(outer)
                                                      .setInner(List.of(inner))
                                                      .setDiagonals(diagonals)
                                                      .build();
        final Robot droid = new Robot(model);
        final FinishLine finish;
        droid.setGround(platform);
        droid.setPos(p(0, -3f * size - 1.5f));
        droid.setRotation(-HALF_PI);
        final Position pos = even(size) ? p(0f, 1.5f) : p(0f, -1.5f);
        finish = new FinishLine.Builder().setModel(model)
                                         .setPos(pos)
                                         .setSize(2f, 2f)
                                         .setElevation(height(pos) + 0.05f)
                                         .build();
        map.add(platform);
        map.add(droid);
        map.add(finish);
        map.setDroid(droid);
        return map;
    }

    /**
     * calculating the height
     *
     * @param p Position
     * @return height
     */
    private float height(Position p) {
        final float r = 3f * size + 1.5f;
        return 3f * cos(p.getX() / (TRACK_WIDTH * 3f) * PI) + 0.2f * p.getY();
    }

    /**
     * calculating diagonals(ElevatedSegments) from inner and outer ElevatedPoints
     */
    private void makeDiagonals() {
        final var it1 = inner.iterator();
        final var it2 = outer.iterator();
        while (it1.hasNext() && it2.hasNext())
            diagonals.add(new ElevatedSegment(it1.next(), it2.next()));
        if (it1.hasNext() || it2.hasNext())
            throw new IllegalStateException("different number of points in inner and outer border");
    }

    /**
     * generates lower ElevatedPoints for inner and outer
     */
    private void makeLower() {
        final float innerRadius = 3f * size + 1f;
        final float outerRadius = innerRadius + 1f;
        final float r = 0.5f * (innerRadius + outerRadius);
        final int n = (3 * size + 1) * STEPS;
        for (int i = 0; i < n; i++) {
            final float angle = (-PI * i) / n;
            final float h = height(p(r * cos(angle), r * sin(angle)));
            inner.add(p(innerRadius * cos(angle), innerRadius * sin(angle), h));
            outer.add(p(outerRadius * cos(angle), outerRadius * sin(angle), h));
        }
    }

    /**
     * generates upper ElevatedPoints for inner and outer
     */
    private void makeUpper() {
        for (int j = -size; j <= size; j++) {
            final float innerRadius;
            final float outerRadius;
            final float max;
            if (even(j) != even(size)) {
                innerRadius = 2f;
                outerRadius = 1f;
                max = -PI;
            }
            else {
                innerRadius = 1f;
                outerRadius = 2f;
                max = PI;
            }
            final float r = 0.5f * (innerRadius + outerRadius);
            for (int i = STEPS; i > 0; i--) {
                final float angle = (max * i) / STEPS;
                final float h = height(p(3 * j + r * cos(angle), r * sin(angle)));
                inner.add(p(3 * j + innerRadius * cos(angle), innerRadius * sin(angle), h));
                outer.add(p(3 * j + outerRadius * cos(angle), outerRadius * sin(angle), h));
            }
        }
    }

    /**
     * checks if v is an even number
     *
     * @param v int to check
     * @return boolean if v is even or not
     */
    private static boolean even(int v) {
        return v % 2 == 0;
    }

    /**
     * creates Position from coordinates
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Position
     */
    private Position p(float x, float y) {
        return new FloatPoint(x * TRACK_WIDTH, -y * TRACK_WIDTH);
    }

    /**
     * creates Elevated Position from coordinates
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param h z-coordinate/elevation
     * @return ElevatedPoint
     */
    private ElevatedPoint p(float x, float y, float h) {
        return new ElevatedPoint(p(x, y), h - TRACK_HEIGHT, h);
    }
}
