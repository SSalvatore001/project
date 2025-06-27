//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

/**
 * Listener interface for all events implemented by subclasses of {@linkplain GameEvent}.
 */
public interface GameEventListener {
    /**
     * Indicates that the game map has changed
     *
     * @param event the received event
     */
    default void received(MapChangedEvent event) { /* do nothing */ }

    /**
     * Indicates that a weapon has been fired
     *
     * @param event the received event
     */
    default void received(WeaponFiredEvent event) { /* do nothing */ }

    /**
     * Indicates that an item has been destroyed
     *
     * @param event the received event
     */
    default void received(ItemDestroyedEvent event) { /* do nothing */ }

    /**
     * Indicates that an item has been hit.
     *
     * @param event the received event
     */
    default void received(HitEvent event) { /* do nothing */ }

    /**
     * Indicates that an item has been added to a map.
     *
     * @param added the received event
     */
    default void received(ItemAddedEvent added) { /* do nothing */ }

    /**
     * Indicates that a pathfinder has finished computing a navigation path.
     *
     * @param event the received event
     */
    default void received(PathComputed event) { /* do nothing */ }
}
