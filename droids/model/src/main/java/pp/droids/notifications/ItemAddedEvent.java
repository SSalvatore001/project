//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

import pp.droids.model.DroidsMap;
import pp.droids.model.item.Item;

/**
 * Event when an item is added to a map.
 *
 * @param item the added item
 * @param map  the map that got the additional item
 */
public record ItemAddedEvent(Item item, DroidsMap map) implements GameEvent {
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
