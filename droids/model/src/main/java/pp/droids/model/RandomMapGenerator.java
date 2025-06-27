//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import pp.droids.model.item.CircularItem;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.util.ElevatedPoint;
import pp.util.Position;
import pp.util.RandomPositionIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static pp.util.FloatMath.TWO_PI;

/**
 * Generates a randomized game map including walls, obstacles, enemies, a droid, and a finish line.
 * <p>
 * The resulting map is compatible with {@link DroidsModel} and ensures that all items are placed
 * in valid, non-overlapping positions. Items are distributed using a shuffled position iterator.
 * </p>
 */
public class RandomMapGenerator {

    /**
     * Half the width of the surrounding wall, if enabled.
     */
    private static final float WALL_WIDTH = 0.15f;

    /**
     * Coordinates used to generate a standard maze polygon.
     */
    private static final List<ElevatedPoint> POSITIONS =
            List.of(p(5.85f, 12.15f), p(2.85f, 12.15f), p(2.85f, 9f), p(3.15f, 9f),
                    p(3.15f, 11.85f), p(9f, 11.85f), p(9f, 12.15f), p(6.15f, 12.15f),
                    p(6.15f, 15.15f), p(-0.15f, 15.15f), p(-0.15f, -0.15f), p(15.15f, -0.15f),
                    p(15.15f, 15.15f), p(9f, 15.15f), p(9f, 14.85f), p(14.85f, 14.85f),
                    p(14.85f, 12.15f), p(11.85f, 12.15f), p(11.85f, 9.15f), p(6f, 9.15f),
                    p(6f, 8.85f), p(12.15f, 8.85f), p(12.15f, 11.85f), p(14.85f, 11.85f),
                    p(14.85f, 3.15f), p(12.15f, 3.15f), p(12.15f, 6.15f), p(3f, 6.15f),
                    p(3f, 5.85f), p(8.85f, 5.85f), p(8.85f, 3f), p(9.15f, 3f),
                    p(9.15f, 5.85f), p(11.85f, 5.85f), p(11.85f, 2.85f), p(14.85f, 2.85f),
                    p(14.85f, 0.15f), p(6.15f, 0.15f), p(6.15f, 3f), p(5.85f, 3f),
                    p(5.85f, 0.15f), p(0.15f, 0.15f), p(0.15f, 2.85f), p(3f, 2.85f),
                    p(3f, 3.15f), p(0.15f, 3.15f), p(0.15f, 14.85f), p(5.85f, 14.85f));

    private static final float FINISH_DX = 4f;
    private static final float FINISH_DY = 2f;

    private final DroidsModel model;
    private final Random random = new Random();
    private final DroidsMap map;
    private final FinishLine finishLine;
    private final Iterator<Position> it;

    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;

    /**
     * Constructs a random map generator for the given model.
     *
     * @param model the game model to populate
     */
    private RandomMapGenerator(DroidsModel model) {
        this.model = model;
        map = new DroidsMap(randomMapType());
        it = RandomPositionIterator.floatPoints(model.getConfig().getWidth(), model.getConfig().getHeight());
        xMin = -0.5f;
        xMax = model.getConfig().getWidth() - 0.5f;
        yMin = -0.5f;
        yMax = model.getConfig().getHeight() - 0.5f;

        List<ElevatedPoint> outer = List.of(pe(xMin, yMin), pe(xMax, yMin), pe(xMax, yMax), pe(xMin, yMax));
        final Polygon platform = new Polygon.Builder().setModel(model)
                                                      .setSpec(map.getMapType().platformSpec())
                                                      .setOuter(outer)
                                                      .build();
        map.add(platform);

        finishLine = new FinishLine.Builder().setModel(model)
                                             .setPos(xMax - 0.5f * FINISH_DX - 0.5f,
                                                     yMin + 0.5f * FINISH_DY + 0.5f)
                                             .setSize(FINISH_DX, FINISH_DY)
                                             .setElevation(0.05f)
                                             .build();
        map.add(finishLine);

        if (random.nextBoolean())
            map.add(makeWall(model, map.getMapType().wallSpec()));

        if (model.getConfig().hasMaze())
            map.add(new Polygon.Builder().setModel(model)
                                         .setSpec(map.getMapType().wallSpec())
                                         .setOuter(POSITIONS)
                                         .build());

        // Add obstacles
        for (int i = 0; i < model.getConfig().getNumObstacles(); i++)
            addCircularItem(new Obstacle(model), platform);

        // Add enemies
        for (int i = 0; i < model.getConfig().getNumEnemies(); i++)
            addCircularItem(new Robot(model), platform);

        setDroid(platform);
    }

    /**
     * Selects a random {@link MapType}.
     *
     * @return a randomly chosen map type
     */
    private MapType randomMapType() {
        return MapType.values()[random.nextInt(MapType.values().length)];
    }

    /**
     * Utility for creating a base polygon corner with a lowered elevation.
     */
    private static ElevatedPoint pe(float x, float y) {
        return new ElevatedPoint(x, y, -0.5f, 0f);
    }

    /**
     * Utility for creating a polygon point .
     */
    private static ElevatedPoint p(float x, float y) {
        return new ElevatedPoint(x, y, -0.5f, Polygon.HEIGHT);
    }

    /**
     * Generates a new random map for the given model.
     *
     * @param model the game model
     * @return a new random {@link DroidsMap}
     */
    public static DroidsMap createMap(DroidsModel model) {
        return new RandomMapGenerator(model).map;
    }

    /**
     * Places the player-controlled droid in a valid random position on the map.
     *
     * @param ground the polygon on which to place the droid
     */
    private void setDroid(Polygon ground) {
        Robot droid = new Robot(model);
        droid.setGround(ground);
        map.add(droid);
        map.setDroid(droid);
        droid.setRotation(random.nextFloat() * TWO_PI);

        while (it.hasNext()) {
            Position pos = it.next();
            droid.setPos(pos);
            if (validPosition(droid))
                return;
        }
    }

    /**
     * Adds a circular item (enemy or obstacle) to the map at a valid position.
     *
     * @param item   the item to add
     * @param ground the ground polygon for placement
     */
    private void addCircularItem(CircularItem item, Polygon ground) {
        item.setGround(ground);
        item.setRotation(random.nextFloat() * TWO_PI);
        while (it.hasNext()) {
            final Position pos = it.next();
            item.setPos(pos.getX(), pos.getY());
            if (validPosition(item)) {
                map.add(item);
                return;
            }
        }
    }

    /**
     * Determines whether the item can be placed at its current position.
     *
     * @param item the item to test
     * @return {@code true} if the position is within bounds and not overlapping
     */
    private boolean validPosition(CircularItem item) {
        final float x = item.getX();
        final float y = item.getY();
        final float r = item.getRadius();
        return finishLine.distanceFrom(item) > r &&
               x - r >= xMin &&
               x + r <= xMax &&
               y - r >= yMin &&
               y + r <= yMax &&
               map.getItems().stream().noneMatch(i -> i.overlap(i, item));
    }

    /**
     * Creates a rectangular wall around the play area.
     *
     * @param model    the game model
     * @param wallSpec specification string for wall appearance
     * @return a wall polygon
     */
    private Polygon makeWall(DroidsModel model, String wallSpec) {
        final var outer = List.of(p(xMin - WALL_WIDTH, yMin - WALL_WIDTH),
                                  p(xMax + WALL_WIDTH, yMin - WALL_WIDTH),
                                  p(xMax + WALL_WIDTH, yMax + WALL_WIDTH),
                                  p(xMin - WALL_WIDTH, yMax + WALL_WIDTH));
        final var inner = List.of(p(xMin + WALL_WIDTH, yMin + WALL_WIDTH),
                                  p(xMin + WALL_WIDTH, yMax - WALL_WIDTH),
                                  p(xMax - WALL_WIDTH, yMax - WALL_WIDTH),
                                  p(xMax - WALL_WIDTH, yMin + WALL_WIDTH));
        return new Polygon.Builder().setModel(model)
                                    .setSpec(wallSpec)
                                    .setOuter(outer)
                                    .setInner(List.of(inner))
                                    .build();
    }
}
