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
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;
import static pp.util.FloatMath.HALF_PI;
import static pp.util.FloatMath.TWO_PI;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * Generator for spiral tracks.
 */
public class SpiralGenerator {
    private static final Logger LOGGER = System.getLogger(SpiralGenerator.class.getName());
    private static final int MAX_SIZE = 3;
    private static final int STEPS = 40;
    private static final float SPIRAL_FACTOR = 0.65f;
    private static final float SPIRAL_ADD = 3f;
    private static final float SLOPE_FACTOR = 0.5f;
    private static final float TRACK_WIDTH = 4f;
    private static final float TRACK_HEIGHT = 0.5f;
    private static final float WALL_WIDTH = 0.3f;
    private static final float WALL_HEIGHT = 0.5f;

    private final int n;

    /**
     * Main method of SpiralGenerator
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
     * creates a map with and without walls
     *
     * @param size spiral size
     * @throws IOException saving the generated map got interrupted
     */
    private static void generate(int size) throws IOException {
        generate(size, true);
        generate(size, false);
    }

    /**
     * creating and saving the track
     *
     * @param size spiral size
     * @throws IOException saving the generated map got interrupted
     */
    private static void generate(int size, boolean withWall) throws IOException {
        final DroidsModel model = new DroidsModel(new JsonSerializer());
        model.setDroidsMap(new SpiralGenerator(size).makeMap(model, withWall));
        final File file = new File(String.format("maps/%sspiral%d.json", withWall ? "wall-" : "", size));
        model.saveMap(file);
        LOGGER.log(Level.INFO, "File {0} generated", file); //NON-NLS
    }

    /**
     * calculating landingLength
     *
     * @return landingLength
     */
    private float landingLength() {
        return n * SPIRAL_FACTOR * TWO_PI + SPIRAL_ADD;
    }

    /**
     * Class for making a Platform for the SpiralGenerator
     */
    private class PlatformMaker {
        private final LinkedList<ElevatedPoint> points = new LinkedList<>();
        private final List<ElevatedSegment> diagonals = new ArrayList<>();
        private ElevatedPoint p1 = null;
        private ElevatedPoint p2 = null;

        /**
         * constructor for PlatformMaker
         */
        PlatformMaker() {
            for (int i = 0; i <= n * STEPS; i++)
                step(i);
            points.addFirst(new ElevatedPoint(p1.x(), p1.y() + landingLength(), p1.bottom(), p1.top()));
            points.addLast(new ElevatedPoint(p2.x(), p2.y() + landingLength(), p2.bottom(), p2.top()));
        }

        /**
         * creating Points between first and last Point
         *
         * @param i number of step
         */
        private void step(float i) {
            final float angle = TWO_PI * i / STEPS;
            final float r1 = SPIRAL_FACTOR * angle + SPIRAL_ADD - 0.5f * TRACK_WIDTH;
            final float r2 = r1 + TRACK_WIDTH;
            final float h = SLOPE_FACTOR * angle;
            final float cos = cos(angle);
            final float sin = sin(angle);
            p1 = new ElevatedPoint(r1 * cos, r1 * sin, h - TRACK_HEIGHT, h);
            p2 = new ElevatedPoint(r2 * cos, r2 * sin, h - TRACK_HEIGHT, h);
            points.addFirst(p1);
            points.addLast(p2);
            if (i != 0)
                diagonals.add(new ElevatedSegment(p1, p2));
        }

        /**
         * creating Polygon for Platform
         *
         * @param model DroidsModel
         * @return generated Polygon
         */
        Polygon makePolygon(DroidsModel model) {
            return new Polygon.Builder().setModel(model)
                                        .setSpec(Spec.STONE)
                                        .setOuter(points)
                                        .setDiagonals(diagonals)
                                        .build();
        }

        /**
         * get initial Position
         *
         * @return Position
         */
        Position initPos() {
            return new FloatPoint(p1.x() + 0.5f, p1.y() + landingLength() - 0.5f);
        }
    }

    /**
     * Class for creating Walls for the SpiralGenerator
     */
    private class Wall1Maker {
        private final LinkedList<ElevatedPoint> points = new LinkedList<>();
        private final List<ElevatedSegment> diagonals = new ArrayList<>();

        /**
         * constructor for WallMaker
         */
        public Wall1Maker() {
            for (int i = 0; i <= n * STEPS; i++)
                step(i);
        }

        /**
         * creating and saving Points/track
         *
         * @param i number of step
         */
        private void step(float i) {
            final float angle = TWO_PI * i / STEPS;
            final float h = SLOPE_FACTOR * angle;
            final float cos = cos(angle);
            final float sin = sin(angle);
            final float r1 = SPIRAL_FACTOR * angle + SPIRAL_ADD - 0.5f * (TRACK_WIDTH + WALL_WIDTH);
            final float r2 = r1 + WALL_WIDTH;
            final float bottom = max(-WALL_HEIGHT, h - WALL_HEIGHT - TWO_PI * SLOPE_FACTOR);
            final var p1 = new ElevatedPoint(r1 * cos, r1 * sin, bottom, h + WALL_HEIGHT);
            final var p2 = new ElevatedPoint(r2 * cos, r2 * sin, bottom, h + WALL_HEIGHT);
            points.addFirst(p1);
            points.addLast(p2);
            if (i > 0 && i < n * STEPS)
                diagonals.add(new ElevatedSegment(p1, p2));
        }

        /**
         * creating Polygon for Platform
         *
         * @param model DroidsModel
         * @return generated Polygon
         */
        Polygon makePolygon(DroidsModel model) {
            return new Polygon.Builder().setModel(model)
                                        .setSpec(Spec.WALL)
                                        .setOuter(points)
                                        .setDiagonals(diagonals)
                                        .build();
        }
    }

    /**
     * Class for creating Walls for the SpiralGenerator
     */
    private class Wall2Maker {
        private final LinkedList<ElevatedPoint> points = new LinkedList<>();
        private final List<ElevatedSegment> diagonals = new ArrayList<>();

        /**
         * constructor for WallMaker
         */
        public Wall2Maker() {
            for (int i = 0; i <= STEPS; i++)
                step(i);
        }

        /**
         * creating and saving Points/track
         *
         * @param i number of step
         */
        private void step(float i) {
            final float angle = TWO_PI * (n - 1 + i / STEPS);
            final float h = SLOPE_FACTOR * angle;
            final float cos = cos(angle);
            final float sin = sin(angle);
            final float r1 = SPIRAL_FACTOR * (angle + TWO_PI) + SPIRAL_ADD - 0.5f * (TRACK_WIDTH + WALL_WIDTH);
            final float r2 = r1 + WALL_WIDTH;
            final var p1 = new ElevatedPoint(r1 * cos, r1 * sin, h - WALL_HEIGHT, h + WALL_HEIGHT);
            final var p2 = new ElevatedPoint(r2 * cos, r2 * sin, h - WALL_HEIGHT, h + WALL_HEIGHT);
            points.addFirst(p1);
            points.addLast(p2);
            if (i > 0 && i < STEPS)
                diagonals.add(new ElevatedSegment(p1, p2));
        }

        /**
         * creating Polygon for Platform
         *
         * @param model DroidsModel
         * @return generated Polygon
         */
        Polygon makePolygon(DroidsModel model) {
            return new Polygon.Builder().setModel(model)
                                        .setSpec(Spec.WALL)
                                        .setOuter(points)
                                        .setDiagonals(diagonals)
                                        .build();
        }
    }

    /**
     * Creates a SpiralGenerator with the specified track width and its size parameter.
     */
    private SpiralGenerator(int n) {
        this.n = n;
    }

    /**
     * generates the spiral
     *
     * @param model DroidsModel
     * @return generated DroidsMap
     */

    private DroidsMap makeMap(DroidsModel model, boolean withWalls) {
        final DroidsMap map = new DroidsMap(MapType.CASTLE);
        final PlatformMaker platformMaker = new PlatformMaker();
        final Polygon platform = platformMaker.makePolygon(model);
        final Robot droid = new Robot(model);
        droid.setGround(platform);
        droid.setPos(platformMaker.initPos());
        droid.setRotation(-HALF_PI);
        map.add(droid);
        map.setDroid(droid);
        map.add(platform);
        if (withWalls) {
            map.add(new Wall1Maker().makePolygon(model));
            map.add(new Wall2Maker().makePolygon(model));
        }
        map.add(new FinishLine.Builder().setModel(model)
                                        .setPos(SPIRAL_ADD, 0.5f)
                                        .setSize(1f, 1f)
                                        .setElevation(0.4f)
                                        .build());
        return map;
    }
}
