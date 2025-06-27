//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view.radar;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import pp.droids.DroidsApp;
import pp.droids.GameState;
import pp.droids.model.item.Robot;
import pp.util.Position;
import pp.util.Property;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.Preferences;

import static com.jme3.math.FastMath.HALF_PI;
import static pp.util.PreferencesUtils.getPreferences;

/**
 * An {@link AbstractAppState} implementing a radar view in the top-right corner of the GUI.
 * <p>
 * The radar displays droids, obstacles, projectiles, finish lines, and other map elements,
 * as well as optionally the path that a droid is following. The radar view is synchronized
 * with the model and updated on each frame.
 * </p>
 */
public class RadarView extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(RadarView.class.getName());
    private static final Preferences PREFERENCES = getPreferences(RadarView.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS
    private static final String SHOW_PATH_PREF = "show-path"; //NON-NLS
    private static final float SIZE = 0.3f;
    private static final float SPRITE_SIZE = 11f;
    private static final float DIAMETER = 0.4f;

    private DroidsApp app;
    private final Node radarGuiNode = new Node("radarGui");
    private final Node radarNode = new Node("radar"); //NON-NLS
    private final Node centerNode = new Node("center"); //NON-NLS
    private final Node turnNode = new Node("turn"); //NON-NLS
    private final Node pathNode = new Node("path"); //NON-NLS
    private boolean showPath = PREFERENCES.getBoolean(SHOW_PATH_PREF, true);

    /**
     * Checks whether the radar view is enabled in the preferences.
     *
     * @return {@code true} if enabled, {@code false} otherwise
     */
    public static boolean enabledInPreferences() {
        return PREFERENCES.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Initializes the radar view app state.
     *
     * @param stateManager the state manager managing this state
     * @param app          the application instance
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        new RadarSynchronizer(getGameState(), centerNode);
        setupNodes();
        setupBackground();
        setupViewPort();
        if (isEnabled())
            enableState();
    }

    /**
     * Returns whether the radar view is configured to display the droid’s path.
     *
     * @return {@code true} if path display is enabled, {@code false} otherwise
     */
    public boolean showPath() {
        return showPath;
    }

    /**
     * Enables or disables path display in the radar view.
     *
     * @param showPath {@code true} to show paths, {@code false} to hide them
     */
    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
        PREFERENCES.put(SHOW_PATH_PREF, String.valueOf(showPath));
    }

    /**
     * Retrieves the {@link GameState} associated with this app.
     *
     * @return the game state
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Sets up the spatial node hierarchy used in the radar view.
     */
    private void setupNodes() {
        app.getGuiNode().getParent().attachChild(radarGuiNode);
        radarNode.attachChild(centerNode);

        final AppSettings settings = app.getContext().getSettings();
        centerNode.setLocalTranslation(0.5f * settings.getWidth(), 0.5f * settings.getHeight(), 0f);
        centerNode.scale(SPRITE_SIZE / SIZE);

        centerNode.attachChild(turnNode);
        turnNode.attachChild(pathNode);
    }

    /**
     * Sets up the radar background geometry with semi-transparency.
     */
    private void setupBackground() {
        final Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"); //NON-NLS
        mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f)); //NON-NLS
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        final AppSettings settings = app.getContext().getSettings();
        final Geometry background = new Geometry("RadarBackground", new Quad(settings.getWidth(), settings.getHeight()));
        background.setMaterial(mat);
        background.setLocalTranslation(0f, 0f, -1f);
        background.setCullHint(CullHint.Never);

        radarNode.attachChild(background);
    }

    /**
     * Sets up the dedicated viewport for rendering the radar in the top-right corner.
     */
    private void setupViewPort() {
        final Camera guiCam = app.getGuiViewPort().getCamera().clone();
        guiCam.setViewPort(1f - SIZE, 1f, 1f - SIZE, 1f);

        final ViewPort radarGuiVP = app.getRenderManager().createPostView("radar-gui", guiCam); //NON-NLS
        radarGuiVP.setClearFlags(false, false, false);
        radarGuiVP.attachScene(radarGuiNode);
    }

    /**
     * Enables or disables the radar app state and updates preference storage.
     *
     * @param enabled {@code true} to enable the radar, {@code false} to disable it
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;

        super.setEnabled(enabled);
        LOGGER.log(Level.INFO, "Radar view enabled: {0}", enabled); //NON-NLS
        PREFERENCES.put(ENABLED_PREF, String.valueOf(enabled));

        if (enabled)
            enableState();
        else
            disableState();
    }

    /**
     * Activates the radar view by attaching it to the GUI node.
     */
    private void enableState() {
        radarGuiNode.attachChild(radarNode);
    }

    /**
     * Deactivates the radar view by detaching it from the GUI node.
     */
    private void disableState() {
        radarGuiNode.detachChild(radarNode);
    }

    /**
     * Updates the radar view each frame.
     * <p>
     * Clears and optionally redraws the droid's current path.
     *
     * @param delta time since last update (in seconds)
     */
    @Override
    public void update(float delta) {
        pathNode.detachAllChildren();

        if (showPath) {
            final Robot droid = getGameState().getModel().getDroidsMap().getDroid();
            if (droid != null && droid.getPath() != null) {
                adjustView(droid);
                addPath(droid);
            }
        }
    }

    /**
     * Adds the droid's path to the path node as a sequence of lines and ellipses.
     *
     * @param droid the droid whose path should be rendered
     */
    private void addPath(Robot droid) {
        Position prev = droid;
        for (Position p : droid.getPath()) {
            pathNode.attachChild(app.getDraw().makeLine(prev, p, 3f, ColorRGBA.Pink));
            pathNode.attachChild(app.getDraw().makeEllipse(p.getX(), p.getY(), 0f, DIAMETER, DIAMETER, ColorRGBA.Pink));
            prev = p;
        }
    }

    /**
     * Adjusts the path view by translating and rotating the view based on the droid's position and heading.
     *
     * @param droid the droid to use for transformation
     */
    private void adjustView(Robot droid) {
        pathNode.setLocalTranslation(-droid.getX(), -droid.getY(), 0f);
        final Quaternion rot = new Quaternion();
        rot.fromAngleAxis(HALF_PI - droid.getRotation(), Vector3f.UNIT_Z);
        turnNode.setLocalRotation(rot);
    }

    /**
     * Returns a {@link Property} representing whether the radar path should be shown.
     *
     * @return a Boolean property controlling the visibility of the droid's path in the radar view
     */
    public Property<Boolean> getShowPathProperty() {
        return new Property<>() {
            @Override
            public String getName() {
                return "show path in radar view";
            }

            @Override
            public Boolean getValue() {
                return showPath;
            }

            @Override
            public void setValue(Boolean value) {
                setShowPath(value);
            }
        };
    }
}
