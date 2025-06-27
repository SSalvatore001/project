//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pp.droids.model.item.Item;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.PathComputed;
import pp.util.ElevatedPoint;
import pp.util.FloatPoint;
import pp.util.Position;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.fail;
import static pp.droids.model.Util.makeGround;
import static pp.util.FloatMath.abs;

/**
 * Unit tests for the {@code Dog} behavior in a controlled environment,
 * using reflection to instantiate the {@code Dog} class.
 * <p>
 * Validates two key behaviors:
 * <ul>
 *   <li><b>Inactivity:</b> The dog must remain stationary when the
 *   player droid is hidden by obstacles.</li>
 *   <li><b>Pursuit:</b> The dog must chase the droid when it becomes
 *   visible, ensuring it does not freeze and stays within range.</li>
 * </ul>
 * </p>
 */
class DogTest {
    /**
     * Logger to record test diagnostics and path computations.
     */
    private static final Logger LOGGER = System.getLogger(DogTest.class.getName());

    /**
     * Width of the test map in world units.
     */
    private static final int WIDTH = 15;
    /**
     * Height of the test map in world units.
     */
    private static final int HEIGHT = 25;
    /**
     * Simulation time step (seconds) for each update cycle.
     */
    private static final float DELTA_TIME = 0.05f;
    /**
     * Fully qualified class name of the student-provided Dog implementation.
     */
    private static final String DOG_CLASS = "pp.droids.model.item.Dog";

    private DroidsModel gameModel;
    private Robot droid;
    private Item dog;
    private Position savedDogPos;
    private float savedDogRotation;
    private float currentTime = 0f;
    private float savedTime = 0f;

    /**
     * Sets up a fresh game model, ground, droid, wall, and dog before each test.
     * <p>
     * Uses a single-threaded executor for deterministic pathfinding,
     * logs path computations, and constructs a map with an obstacle.
     * </p>
     */
    @BeforeEach
    void setUp() {
        // Single-thread executor ensures predictable future completion
        gameModel = new DroidsModel(Serializer.getInstance()) {
            private ExecutorService service;

            @Override
            public ExecutorService getExecutor() {
                if (service == null)
                    service = Executors.newSingleThreadExecutor();
                return service;
            }
        };
        // Log any computed paths for debugging purposes
        gameModel.addGameEventListener(new GameEventListener() {
            @Override
            public void received(PathComputed event) {
                LOGGER.log(Level.INFO, "Computed path: {0}", event.pathfinder().getPath());
            }
        });

        // Prepare flat ground for navigation
        final Polygon ground = makeGround(gameModel, WIDTH, HEIGHT);

        // Create and position the player-controlled droid
        droid = new Robot(gameModel);
        droid.setGround(ground);

        // Instantiate the dog via reflection and set its ground
        dog = makeDog();
        dog.setGround(ground);

        // Assemble the map: ground, droid, obstacle wall, and dog
        final DroidsMap map = new DroidsMap();
        map.add(ground);
        map.add(droid);
        map.add(makeWall());  // obstacle between dog and droid
        map.add(dog);
        map.setDroid(droid);
        gameModel.setDroidsMap(map);
    }

    /**
     * Tests that the dog remains stationary when the droid is occluded by an obstacle.
     * <p>
     * Positions the droid behind a wall relative to the dog and simulates time.
     * The dog's movement distance must stay below an epsilon threshold.
     * </p>
     */
    @Test
    void testDogWait() throws ExecutionException, InterruptedException {
        droid.setPos(12f, 4f);
        dog.setPos(5f, 20f);
        saveDogParameters();
        while (currentTime <= 10f) {
            updateTimeAndModel();
            if (dogMoved())
                fail(String.format("Dog (%f,%f) moved after %f sec while hidden",
                                   dog.getX(), dog.getY(), currentTime));
            waitForFutures();
        }
    }

    /**
     * Tests that the dog pursues the droid when it comes into line of sight.
     * <p>
     * First moves the droid into visibility range, then verifies pursuit behavior,
     * and repeats with a second repositioning to confirm consistent chasing logic.
     * </p>
     */
    @Test
    void testDogRuns() throws ExecutionException, InterruptedException {
        // First phase: droid moves into range
        droid.setPos(5f, 4f);
        dog.setPos(5f, 20f);
        saveDogParameters();
        while (currentTime <= 20f) {
            updateTimeAndModel();
            checkDogMoving(5f);
            waitForFutures();
        }
        // Second phase: new droid position, ensure dog still pursues
        droid.setPos(12f, 4f);
        while (currentTime <= 40f) {
            updateTimeAndModel();
            checkDogMoving(25f);
            waitForFutures();
        }
    }

    /**
     * Ensures the dog has moved within a given time limit, or fails if it "freezes."
     * Also verifies the maximum chase distance does not exceed 4 units after limit time.
     *
     * @param limitTime time threshold after which distance constraint applies
     */
    private void checkDogMoving(float limitTime) {
        if (dogMoved())
            saveDogParameters();
        else if (currentTime - savedTime > 0.5f)
            fail(String.format("Dog (%f,%f) froze after %f sec",
                               dog.getX(), dog.getY(), currentTime));
        if (currentTime > limitTime && droid.distanceTo(dog) > 4f)
            fail(String.format("Dog (%f,%f) too far after %f sec; dist=%f",
                               dog.getX(), dog.getY(), currentTime, droid.distanceTo(dog)));
    }

    /**
     * Checks whether the dog's position and/or rotation changed since the last call to
     * {@linkplain #saveDogParameters()}.
     *
     * @return true when the dog's position and/or rotation changed
     */
    private boolean dogMoved() {
        final float distance = dog.distanceTo(savedDogPos);
        final float rotation = abs(dog.getRotation() - savedDogRotation);
        return distance + rotation > 0.05f;
    }

    /**
     * Advances the simulation clock and updates the game model by one time step.
     */
    private void updateTimeAndModel() {
        gameModel.update(DELTA_TIME);
        currentTime += DELTA_TIME;
    }

    /**
     * Records the dog's current position and rotation as well as the simulation time.
     */
    private void saveDogParameters() {
        savedDogPos = new FloatPoint(dog);
        savedDogRotation = dog.getRotation();
        savedTime = currentTime;
    }

    /**
     * Blocks until all pending asynchronous pathfinding tasks complete.
     *
     * @throws InterruptedException if thread is interrupted
     * @throws ExecutionException   if computation fails
     */
    private void waitForFutures() throws InterruptedException, ExecutionException {
        gameModel.getExecutor().submit(() -> null).get();
    }

    /**
     * Dynamically instantiates the student-provided Dog class via reflection.
     *
     * @return an {@link Item} instance of the Dog, or null on failure
     */
    private Item makeDog() {
        try {
            final Class<?> cls = Class.forName(DOG_CLASS);
            return (Item) cls.getConstructor(DroidsModel.class).newInstance(gameModel);
        }
        catch (ClassNotFoundException | InvocationTargetException |
               InstantiationException | IllegalAccessException |
               NoSuchMethodException e) {
            fail("Failed to create Dog instance");
        }
        return null;
    }

    /**
     * Constructs a rectangular wall to obstruct the line of sight between dog and droid.
     *
     * @return a {@link Polygon} representing the obstacle wall
     */
    private Polygon makeWall() {
        return new Polygon.Builder().setModel(gameModel)
                                    .setSpec(Spec.WALL)
                                    .setOuter(List.of(pe(6.5f, 6.5f),
                                                      pe(12.5f, 6.5f),
                                                      pe(12.5f, 7.5f),
                                                      pe(6.5f, 7.5f)))
                                    .build();
    }

    /**
     * Utility to create an elevated point for polygon corner.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return an {@link ElevatedPoint} suitable for polygon geometry
     */
    private static ElevatedPoint pe(float x, float y) {
        return new ElevatedPoint(x, y, -0.5f, 5f);
    }
}
