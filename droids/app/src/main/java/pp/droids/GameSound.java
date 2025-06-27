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
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Robot;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.HitEvent;
import pp.droids.notifications.ItemDestroyedEvent;
import pp.droids.notifications.WeaponFiredEvent;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.Preferences;

import static pp.util.PreferencesUtils.getPreferences;

/**
 * An application state that listens to GameEvents and plays sounds triggered by these events.
 */
public class GameSound extends AbstractAppState implements GameEventListener {
    private static final Logger LOGGER = System.getLogger(GameSound.class.getName());
    private static final Preferences PREFERENCES = getPreferences(GameSound.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS

    private DroidsApp app;
    private AudioNode gunSound;
    private AudioNode killedSound;
    private AudioNode hitSound;

    /**
     * Shows, if sound is enabled in the preferences.
     *
     * @return boolean, if sound is enabled
     */
    public static boolean enabledInPreferences() {
        return PREFERENCES.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Turns the sound on or off.
     * It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        LOGGER.log(Level.INFO, "Sound enabled: {0}", enabled); //NON-NLS
        PREFERENCES.put(ENABLED_PREF, String.valueOf(enabled));
    }

    /**
     * Initializes different sounds.
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        gunSound = loadSound("Sound/Effects/Gun.wav"); //NON-NLS
        killedSound = loadSound("Sound/Effects/killed.wav"); //NON-NLS
        hitSound = loadSound("Sound/Effects/hit.wav"); //NON-NLS
    }

    /**
     * Loads the sound.
     *
     * @param name name of the sound
     * @return sound from type AudioNode
     */
    private AudioNode loadSound(String name) {
        final AudioNode sound = new AudioNode(app.getAssetManager(), name,
                                              AudioData.DataType.Buffer);
        sound.setLooping(false);
        sound.setPositional(false);
        return sound;
    }

    /**
     * Recognizes fired shots, destroyed enemies and hits. If an event is recognized, it calls a method to play the  particular sound.
     *
     * @param model the droids model
     */
    public void register(DroidsModel model) {
        model.addGameEventListener(this);
    }

    @Override
    public void received(WeaponFiredEvent event) {
        LOGGER.log(Level.INFO, "GameSound::weaponFired : weapon={0}", event.weapon()); //NON-NLS
        if (isEnabled())
            gunSound.playInstance();
    }

    @Override
    public void received(ItemDestroyedEvent event) {
        LOGGER.log(Level.INFO, "GameSound::itemDestroyed : item={0}", event.item()); //NON-NLS
        if (isEnabled() && event.item() instanceof Robot)
            killedSound.playInstance();
    }

    @Override
    public void received(HitEvent event) {
        LOGGER.log(Level.INFO, "GameSound::hit : damaged={0}", event.damaged()); //NON-NLS
        if (isEnabled())
            hitSound.playInstance();
    }
}
