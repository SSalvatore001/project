//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Robot;
import pp.util.FloatPoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pp.droids.model.json.Util.EPS;
import static pp.droids.model.json.Util.assertPositionEquals;
import static pp.droids.model.json.Util.checkEqualPositions;
import static pp.droids.model.json.Util.getItems;

public class LoadMapTest {
    private static final String FILE_NAME = "/maps/map.json"; //NON-NLS

    private DroidsModel gameModel;

    @BeforeEach
    public void setUp() throws IOException {
        gameModel = new DroidsModel(new JsonSerializer());
        loadMap(FILE_NAME);
    }

    private void loadMap(String name) throws IOException {
        final InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null)
            throw new IOException("Cannot find " + name);
        gameModel.loadMap(stream);
    }

    @AfterEach
    public void tearDown() {
        gameModel.shutdown();
    }

    @Test
    public void checkLoadedMap() {
        final DroidsMap map = gameModel.getDroidsMap();
        // Check, if map was loaded successfully
        assertNotNull(map);

        // Check, if the size of all loaded items are right
        assertNotNull(gameModel.getDroidsMap().getDroid());
        assertEquals(5, getItems(gameModel, Obstacle.class).size());
        assertEquals(5, getItems(gameModel, Robot.class, map.getDroid()).size());
    }

    @Test
    public void checkLoadedDroid() {
        // Check if Droid is on right position
        assertPositionEquals(new FloatPoint(12f, 7f), gameModel.getDroidsMap().getDroid(), EPS);
    }

    @Test
    public void checkLoadedEnemies() {
        final List<FloatPoint> expected = List.of(new FloatPoint(13, 12),
                                                  new FloatPoint(10, 11),
                                                  new FloatPoint(4, 9),
                                                  new FloatPoint(12, 10),
                                                  new FloatPoint(21, 12));
        checkEqualPositions(expected, getItems(gameModel, Robot.class, gameModel.getDroidsMap().getDroid()));
    }

    @Test
    public void checkLoadedObstacles() {
        final List<FloatPoint> expected = List.of(new FloatPoint(9, 9),
                                                  new FloatPoint(4, 8),
                                                  new FloatPoint(22, 7),
                                                  new FloatPoint(1, 8),
                                                  new FloatPoint(2, 12));
        checkEqualPositions(expected, getItems(gameModel, Obstacle.class));
    }
}
