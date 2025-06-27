//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

import pp.droids.model.item.Item;

/**
 * Event when an item gets destroyed.
 *
 * @param item the destroyed item
 */
public record ItemDestroyedEvent(Item item) implements GameEvent {
    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.received(this);
    }
}
