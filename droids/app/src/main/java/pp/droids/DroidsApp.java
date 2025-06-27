//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pp.dialog.Dialog;
import pp.droids.view.radar.RadarView;
import pp.graphics.Draw;
import pp.util.config.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.logging.LogManager;

import static pp.util.config.Resources.lookup;

/**
 * The main class of the Droids game.
 */
public class DroidsApp extends SimpleApplication {
    private static final Logger LOGGER = System.getLogger(DroidsApp.class.getName());
    private static final File CONFIG_FILE = new File("config.properties");
    private static final String PAUSE = "PAUSE";
    private final DroidsAppConfig config = new DroidsAppConfig();
    private final Node droidsGuiNode = new Node();
    private Draw draw;
    private boolean menuOpened;

    static {
        // Initialize the global resource bundle
        Resources.initialize("droids");
        // Configure logging
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("logging.properties"));
            LOGGER.log(Level.INFO, "Successfully read logging properties"); //NON-NLS
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
    }

    /**
     * Main method of the droids app
     *
     * @param args input args
     */
    public static void main(String[] args) {
        new DroidsApp().start();
    }

    /**
     * Creates a new DroidsApp object.
     */
    private DroidsApp() {
        config.readFromIfExists(CONFIG_FILE);
        LOGGER.log(Level.INFO, "Configuration: {0}", config); //NON-NLS
        setShowSettings(config.getShowSettings());
        setSettings(makeSettings());
    }

    /**
     * load settings from configs
     *
     * @return configured AppSettings
     */
    private AppSettings makeSettings() {
        final AppSettings settings = new AppSettings(true);
        settings.setTitle(lookup("game.name"));
        settings.setResolution(config.getResolutionWidth(), config.getResolutionHeight());
        settings.setFullscreen(config.fullScreen());
        settings.setUseRetinaFrameBuffer(config.useRetinaFrameBuffer());
        settings.setGammaCorrection(config.useGammaCorrection());
        return settings;
    }

    /**
     * Closes the application.
     *
     * @param esc If true, the user pressed ESC to close the application.
     */
    @Override
    public void requestClose(boolean esc) {
        // do nothing
    }

    /**
     * Overrides  {@link com.jme3.app.SimpleApplication#simpleInitApp()}.
     * It initializes a simple app by setting up the gui node, the input and the states.
     */
    @Override
    public void simpleInitApp() {
        draw = new Draw(assetManager);
        Dialog.initialize(this);
        setupGuiNode();
        setupInput();
        setupStates();
    }

    /**
     * Sets up a gui node for the DroidsApp.
     */
    private void setupGuiNode() {
        guiNode.attachChild(droidsGuiNode);
        guiViewPort.detachScene(guiNode);
        guiViewPort.attachScene(droidsGuiNode);
    }

    /**
     * Sets up the input for the DroidsApp.
     */
    private void setupInput() {
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(pauseListener, PAUSE);
    }

    /**
     * Initializes the States and disables them.
     */
    private void setupStates() {
        stateManager.detach(stateManager.getState(StatsAppState.class));
        stateManager.detach(stateManager.getState(DebugKeysAppState.class));
        flyCam.setEnabled(false);
        if (config.getShowStatistics()) {
            final BitmapFont normalFont = assetManager.loadFont("Interface/Fonts/Default.fnt"); //NON-NLS
            final StatsAppState stats = new StatsAppState(getGuiNode(), normalFont);
            stateManager.attach(stats);
        }
        final GameState gameState = new GameState();
        final TextOverlay textOverlay = new TextOverlay();
        final GameSound gameSound = new GameSound();
        final GameInput gameInput = new GameInput();
        final RadarView radarView = new RadarView();
        stateManager.attachAll(gameState, textOverlay, gameSound, gameInput, radarView);
        gameState.setEnabled(false);
        textOverlay.setEnabled(false);
        gameInput.setEnabled(false);
        gameSound.setEnabled(GameSound.enabledInPreferences());
        radarView.setEnabled(RadarView.enabledInPreferences());
    }

    /**
     * Is used to receive an input, when the pause button is pressed and switches States.
     */
    private final ActionListener pauseListener = (name, isPressed, tpf) -> {
        if (PAUSE.equals(name) && isPressed && !Dialog.anyOpenDialog())
            openMenu();
    };

    private void openMenu() {
        stateManager.getState(GameState.class).setEnabled(false);
        new Menu(this).open();
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        if (menuOpened) return;
        menuOpened = true;
        openMenu();
    }

    /**
     * Is a method to get the config.
     *
     * @return Configuration of the game.
     */
    public DroidsAppConfig getConfig() {
        return config;
    }

    /**
     * Returns the effective Gui node of the Droids game.
     * Note that the effective Gui node is the child of the
     * initial Gui node.
     */
    @Override
    public Node getGuiNode() {
        return droidsGuiNode;
    }

    /**
     * Returns the Draw object for drawing graphical primitives.
     */
    public Draw getDraw() {
        return draw;
    }
}
