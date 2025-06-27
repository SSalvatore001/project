//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.droids.model.json.JsonSerializer;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.MapChangedEvent;
import pp.droids.view.MainSynchronizer;
import pp.util.Position;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static com.jme3.math.FastMath.cos;
import static com.jme3.math.FastMath.sin;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;
import static pp.droids.view.CoordinateTransformation.viewToModel;

/**
 * An application state that manages the Droids game view.
 */
public class GameState extends AbstractAppState implements GameEventListener {
    private static final Logger LOGGER = System.getLogger(GameState.class.getName());
    /**
     * Distance of the camera position behind the droid
     */
    private static final float BEHIND_DROID = 2f;
    /**
     * Height of the camera position above ground
     */
    private static final float ABOVE_GROUND = 2f;
    /**
     * How much the camera looks down
     */
    private static final float INCLINATION = 0.5f;

    private DroidsApp app;
    private final Node viewNode = new Node("view"); //NON-NLS
    private DroidsModel model;
    private Scene scene;
    private MainSynchronizer sync;

    /**
     * Returns the Droids app.
     *
     * @return droids app
     */
    public DroidsApp getApp() {
        return app;
    }

    /**
     * Returns the model.
     *
     * @return droids model
     */
    public DroidsModel getModel() {
        return model;
    }

    @Override
    public void received(MapChangedEvent event) {
        reset();
    }

    /**
     * Resets the synchronizer for model and view, the camera and the floor.
     */
    private void reset() {
        adjustCamera();
        scene.selectLook(model.getDroidsMap().getMapType());
    }

    /**
     * Sets up the app state.
     * <p>
     * It overrides {@link AbstractAppState#initialize(AppStateManager, Application)}
     *
     * @param stateManager The state manager
     * @param application  The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        this.app = (DroidsApp) application;
        model = new DroidsModel(new JsonSerializer(), app.getConfig());
        model.loadRandomMap();
        app.getRootNode().attachChild(viewNode);
        app.getStateManager().getState(GameSound.class).register(model);
        model.addGameEventListener(this);
        sync = new MainSynchronizer(this, viewNode);
        scene = new Scene(this, viewNode);
        reset();

        if (isEnabled()) enableState(true);
    }

    /**
     * Cleans up the game state.
     * <p>
     * It overrides {@linkplain AbstractAppState#cleanup()}.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        LOGGER.log(Level.INFO, "called GameState::cleanup"); //NON-NLS
        if (model != null)
            model.shutdown();
    }

    /**
     * Adjusts the camera to see through the droid's eyes.
     */
    private void adjustCamera() {
        final Robot droid = model.getDroidsMap().getDroid();
        final Camera camera = app.getCamera();
        final float angle = droid.getRotation();
        final float cos = BEHIND_DROID * cos(angle);
        final float sin = BEHIND_DROID * sin(angle);
        final float x = droid.getX() - cos;
        final float y = droid.getY() - sin;
        final float elevation = droid.getElevation() + ABOVE_GROUND;
        camera.setLocation(new Vector3f(modelToViewX(x, y),
                                        modelToViewY(x, y) + elevation,
                                        modelToViewZ(x, y)));
        camera.getRotation().lookAt(new Vector3f(modelToViewX(cos, sin),
                                                 modelToViewY(cos, sin) - INCLINATION,
                                                 modelToViewZ(cos, sin)),
                                    Vector3f.UNIT_Y);
        camera.update();
    }

    /**
     * Returns the point of the ground at the specified screen coordinates, ignoring
     * all obstacles etc.
     *
     * @param point a point in screen coordinates
     */
    public Position getGroundPoint(Vector2f point) {
        final Polygon ground = getModel().getDroidsMap().getDroid().getGround();
        if (ground == null) {
            LOGGER.log(Level.WARNING, "droid does not stand on ground"); //NON-NLS
            return null;
        }
        final Spatial groundSpatial = sync.getSpatial(ground);
        if (groundSpatial == null) {
            LOGGER.log(Level.WARNING, "ground is not mapped in view"); //NON-NLS
            return null;
        }
        final Camera camera = app.getCamera();
        final Vector3f origin = camera.getWorldCoordinates(point, 0.0f);
        final Vector3f direction = camera.getWorldCoordinates(point, 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        final Ray ray = new Ray(origin, direction);
        final CollisionResults results = new CollisionResults();
        groundSpatial.collideWith(ray, results);
        if (results.size() > 0)
            return viewToModel(results.getCollision(0).getContactPoint());
        return null;
    }

    /**
     * Enables or disables the game state.
     * <p>
     * It overrides {@link AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the game state or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) enableState(enabled);
    }

    /**
     * Enables or disables this GameState.
     */
    private void enableState(boolean enabled) {
        getTextOverlay().setEnabled(enabled);
        getGameInput().setEnabled(enabled);
    }

    /**
     * Updates the synchronizes and the model and enabled the game input, if the game isn't over.
     *
     * @param delta Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        scene.update(delta);
        if (!model.isGameOver()) {
            model.update(delta);
            adjustCamera();
        }
        getGameInput().setEnabled(!model.isGameOver());
    }

    /**
     * Returns the scene.
     */
    Scene getScene() {
        return scene;
    }

    /**
     * Gives the game input from the state manager of the app.
     *
     * @return game input
     */
    private GameInput getGameInput() {
        return app.getStateManager().getState(GameInput.class);
    }

    /**
     * Gives the text overlay from the state manager of the app.
     *
     * @return text overlay
     */
    private TextOverlay getTextOverlay() {
        return app.getStateManager().getState(TextOverlay.class);
    }
}
