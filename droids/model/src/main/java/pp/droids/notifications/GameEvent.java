//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

/**
 * An interface used for all game events.
 */
public interface GameEvent {
    /**
     * Abstract method to notify the game event listener of the event.
     *
     * @param listener the game event listener
     */
    void notify(GameEventListener listener);
}
