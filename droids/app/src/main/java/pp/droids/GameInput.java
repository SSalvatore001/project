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
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import pp.droids.model.item.Item;
import pp.droids.model.item.Robot;
import pp.droids.model.item.ShooterBehavior;
import pp.droids.model.item.Turn;
import pp.droids.model.item.Walk;
import pp.droids.view.radar.RadarView;
import pp.util.Position;

/**
 * An {@link AbstractAppState} that maps user keyboard and mouse input
 * to game actions such as moving, shooting, toggling views, and navigation.
 */
class GameInput extends AbstractAppState {

    // Input mapping names
    private static final String SHOOT = "SHOOT";
    private static final String LEFT = "LEFT";
    private static final String RIGHT = "RIGHT";
    private static final String FORWARD = "FORWARD";
    private static final String BACKWARD = "BACKWARD";
    private static final String MUTE = "MUTE";
    private static final String RADAR_MAP = "RADAR";
    private static final String NAVIGATE = "NAVIGATE";
    private static final String PATH = "PATH";
    private static final String SHOOTER = "SHOOTER";

    /**
     * Reference to the main application.
     */
    private DroidsApp app;

    /**
     * Initializes key and mouse mappings when the state is attached.
     *
     * @param stateManager the AppStateManager
     * @param app          the Application instance
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        final InputManager inputManager = app.getInputManager();

        inputManager.addMapping(SHOOT, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(PATH, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping(MUTE, new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping(SHOOTER, new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping(RADAR_MAP, new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping(NAVIGATE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        if (isEnabled()) enableState();
    }

    /**
     * Enables or disables this state and registers or unregisters listeners accordingly.
     *
     * @param enabled {@code true} to attach listeners; {@code false} to remove them
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) {
            if (enabled)
                enableState();
            else
                disableState();
        }
    }

    /**
     * Attaches input listeners to receive analog and action events.
     */
    private void enableState() {
        final InputManager inputManager = app.getInputManager();
        inputManager.addListener(analogListener, SHOOT, LEFT, RIGHT, FORWARD, BACKWARD);
        inputManager.addListener(actionListener, MUTE, RADAR_MAP, NAVIGATE, PATH, SHOOTER);
    }

    /**
     * Detaches input listeners and ensures cursor is visible.
     */
    private void disableState() {
        final InputManager inputManager = app.getInputManager();
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
        inputManager.setCursorVisible(true);
    }

    /**
     * Handles continuous (analog) input such as movement and shooting.
     * <ul>
     *   <li>Space: fire weapon</li>
     *   <li>A/Left: turn left</li>
     *   <li>D/Right: turn right</li>
     *   <li>W/Up: move forward</li>
     *   <li>S/Down: move backward</li>
     * </ul>
     */
    private final AnalogListener analogListener = (name, value, tpf) -> {
        switch (name) {
            case SHOOT -> getDroid().getWeapon().fire();
            case LEFT -> getDroid().turn(Turn.LEFT);
            case RIGHT -> getDroid().turn(Turn.RIGHT);
            case FORWARD -> getDroid().walk(Walk.FORWARD);
            case BACKWARD -> getDroid().walk(Walk.BACKWARD);
            default -> { /* no-op */ }
        }
    };

    /**
     * Handles discrete (action) input such as toggles and navigation clicks.
     * <ul>
     *   <li>M: mute/unmute sound</li>
     *   <li>R: toggle radar view</li>
     *   <li>P: toggle path visualization</li>
     *   <li>E: enable/disable enemy shooters</li>
     *   <li>Left-click: navigate to clicked ground point</li>
     * </ul>
     */
    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (!isPressed) return;
        switch (name) {
            case MUTE -> toggleMuted();
            case RADAR_MAP -> toggleRadarMap();
            case NAVIGATE -> navigate();
            case PATH -> togglePathView();
            case SHOOTER -> toggleShooters();
            default -> { /* no-op */ }
        }
    };

    /**
     * Toggles shooter behavior for all enemy robots.
     * <p>
     * If an enemy has no behavior, assigns {@link ShooterBehavior};
     * otherwise clears its behavior.
     * </p>
     */
    private void toggleShooters() {
        for (Item item : getGameState().getModel().getDroidsMap().getItems())
            if (item instanceof Robot robot && robot != getDroid())
                if (robot.getBehavior() == null)
                    robot.setBehavior(new ShooterBehavior(robot));
                else
                    robot.setBehavior(null);
    }

    /**
     * Toggles the debug visualization of the droid's path in the radar.
     */
    private void togglePathView() {
        final RadarView radar = app.getStateManager().getState(RadarView.class);
        radar.setShowPath(!radar.showPath());
    }

    /**
     * Toggles the radar minimap display on and off.
     */
    private void toggleRadarMap() {
        final RadarView radarView = app.getStateManager().getState(RadarView.class);
        radarView.setEnabled(!radarView.isEnabled());
    }

    /**
     * Toggles game sound mute/unmute.
     */
    private void toggleMuted() {
        final GameSound sound = app.getStateManager().getState(GameSound.class);
        sound.setEnabled(!sound.isEnabled());
    }

    /**
     * Issues a navigation command to the player droid to move to the floor point under the cursor.
     */
    private void navigate() {
        final Vector2f cursorPos = app.getInputManager().getCursorPosition();
        final Position floorPoint = getGameState().getGroundPoint(cursorPos);
        if (floorPoint != null)
            getDroid().navigateTo(floorPoint);
    }

    /**
     * Retrieves the current {@link GameState} instance.
     *
     * @return the GameState from the AppStateManager
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Retrieves the player-controlled droid from the model.
     *
     * @return the player droid
     */
    private Robot getDroid() {
        return getGameState().getModel().getDroidsMap().getDroid();
    }
}
