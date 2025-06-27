//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import pp.droids.model.item.Item;
import pp.droids.model.item.Robot;
import pp.droids.notifications.GameEvent;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.MapChangedEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents the main game model managing the configuration, map state,
 * event listeners, and overall game logic such as win/loss conditions.
 */
public class DroidsModel {
    /**
     * Logger used for debugging and lifecycle logging.
     */
    private static final Logger LOGGER = System.getLogger(DroidsModel.class.getName());

    /**
     * List of all listeners subscribed to game events.
     */
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * Configuration parameters for this game instance.
     */
    private final DroidsConfig config;

    /**
     * The current game map containing all items and environment data.
     */
    private DroidsMap droidsMap;

    /**
     * The item that has reached the finish line and won, or {@code null} if no winner yet.
     */
    private Item winner;

    /**
     * Thread pool executor for asynchronous operations.
     */
    private ExecutorService executor;

    /**
     * Serializer used to save and load game maps.
     */
    private final Serializer serializer;

    /**
     * Constructs a new game model using the given serializer and configuration.
     *
     * @param serializer the serializer to use for saving/loading maps
     * @param config     the game configuration to use
     */
    public DroidsModel(Serializer serializer, DroidsConfig config) {
        this.config = config;
        this.serializer = serializer;
        setDroidsMap(new DroidsMap());
    }

    /**
     * Constructs a game model with the given serializer and default configuration.
     *
     * @param serializer the serializer to use
     */
    public DroidsModel(Serializer serializer) {
        this(serializer, new DroidsConfig());
    }

    /**
     * Constructs a game model with default configuration and no serialization.
     */
    public DroidsModel() {
        this(new NoopSerializer());
    }

    /**
     * Returns the current game configuration.
     *
     * @return the game configuration
     */
    public DroidsConfig getConfig() {
        return config;
    }

    /**
     * Returns the current droids map.
     *
     * @return the current game map
     */
    public DroidsMap getDroidsMap() {
        return droidsMap;
    }

    /**
     * Returns a lazily initialized thread pool executor for background tasks.
     *
     * @return the executor service
     */
    public ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newCachedThreadPool();
        return executor;
    }

    /**
     * Shuts down the executor service if it has been initialized.
     */
    public void shutdown() {
        LOGGER.log(Level.INFO, "called DroidsModel::shutdown"); //NON-NLS
        if (executor != null)
            executor.shutdown();
    }

    /**
     * Replaces the current game map and resets game state such as winner and timer.
     *
     * @param droidsMap the new map to use
     */
    public void setDroidsMap(DroidsMap droidsMap) {
        final DroidsMap oldMap = this.droidsMap;
        this.droidsMap = droidsMap;
        winner = null;
        notifyListeners(new MapChangedEvent(oldMap, droidsMap));
    }

    /**
     * Generates a random map and sets it as the active game map.
     */
    public void loadRandomMap() {
        setDroidsMap(RandomMapGenerator.createMap(this));
    }

    /**
     * Loads a game map from a file and sets it as the current map.
     *
     * @param file the file containing the map in JSON format
     * @throws IOException if reading the file fails
     */
    public void loadMap(File file) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            loadMap(stream);
        }
    }

    /**
     * Loads a game map from an input stream and sets it as the current map.
     *
     * @param stream the input stream to read the map from
     * @throws IOException if reading fails
     */
    public void loadMap(InputStream stream) throws IOException {
        setDroidsMap(serializer.loadMap(stream, this));
    }

    /**
     * Saves the current game map to a file.
     *
     * @param file the file to write the map to
     * @throws IOException if writing fails
     */
    public void saveMap(File file) throws IOException {
        serializer.saveMap(getDroidsMap(), file);
    }

    /**
     * Updates the game model. Called once per frame.
     *
     * @param deltaTime time since the last frame, in seconds
     */
    public void update(float deltaTime) {
        droidsMap.update(deltaTime);
    }

    /**
     * Registers a new game event listener.
     *
     * @param receiver the listener to add
     */
    public void addGameEventListener(GameEventListener receiver) {
        LOGGER.log(Level.DEBUG, "add listener {0}", receiver); //NON-NLS
        listeners.add(receiver);
    }

    /**
     * Unregisters a game event listener.
     *
     * @param receiver the listener to remove
     */
    public void removeGameEventListener(GameEventListener receiver) {
        LOGGER.log(Level.DEBUG, "remove listener {0}", receiver); //NON-NLS
        listeners.remove(receiver);
    }

    /**
     * Notifies all registered listeners about a game event.
     *
     * @param event the event to dispatch
     */
    public void notifyListeners(GameEvent event) {
        for (GameEventListener listener : new ArrayList<>(listeners))
            event.notify(listener);
    }

    /**
     * Checks if the game has been lost (i.e., the droid is dead or
     * someone else reached the finish line).
     *
     * @return {@code true} if the droid is dead or a non-droid won the game
     */
    public boolean isGameLost() {
        final Robot droid = getDroidsMap().getDroid();
        return droid.isDestroyed() || winner != null && winner != droid;
    }

    /**
     * Checks if the game has been won by the player-controlled droid.
     *
     * @return {@code true} if the droid won the game
     */
    public boolean isGameWon() {
        return winner == getDroidsMap().getDroid();
    }

    /**
     * Returns whether the game is over (won or lost).
     *
     * @return {@code true} if the game has ended
     */
    public boolean isGameOver() {
        return isGameLost() || isGameWon();
    }

    /**
     * Marks the specified item as the winner upon reaching the finish line.
     *
     * @param robot the item that finished the game
     */
    public void reachedFinishLine(Item robot) {
        if (winner == null) {
            winner = robot;
            LOGGER.log(Level.INFO, "{0} wins", winner); //NON-NLS
        }
    }
}
