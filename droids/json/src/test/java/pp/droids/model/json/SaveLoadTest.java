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
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Robot;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static pp.droids.model.json.Util.EPS;
import static pp.droids.model.json.Util.assertPositionEquals;
import static pp.droids.model.json.Util.checkEqualPositions;
import static pp.droids.model.json.Util.getItems;

public class SaveLoadTest {
    static final String FILE_NAME = "test-map.json"; //NON-NLS

    private DroidsModel game;
    private File file;

    @BeforeEach
    public void setUp() {
        // Create random game
        game = new DroidsModel(new JsonSerializer());
        game.loadRandomMap();

        // Check whether the test file already exists
        file = new File(FILE_NAME);
        if (file.exists())
            fail("File " + FILE_NAME + " already exists. Consider deleting it");
    }

    @Test
    public void saveAndLoad() throws IOException {
        game.saveMap(file);
        // Check whether test file exists
        assertTrue(file.exists(), "file " + FILE_NAME + " has not been written");

        // Load the file, which has just been written, into a new game
        final DroidsModel game2 = new DroidsModel(new JsonSerializer());
        game2.loadMap(file);

        checkEqualPositions(getItems(game, Robot.class),
                            getItems(game2, Robot.class));
        checkEqualPositions(getItems(game, Obstacle.class),
                            getItems(game2, Obstacle.class));
        assertPositionEquals(game.getDroidsMap().getDroid(), game2.getDroidsMap().getDroid(), EPS);
    }

    @AfterEach
    public void tearDown() {
        file.deleteOnExit();
        game.shutdown();
    }
}
