//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * Interface of all items that live initially and that are destroyed if
 * they are hit a certain number of times.
 */
public interface DamageReceiver extends Item {
    /**
     * Returns the object providing access to DamageReceiver functionality
     */
    DamageReceiverSupport getDamageReceiverSupport();

    /**
     * Returns the number of lives this item still has.
     *
     * @return remaining number of lives
     */
    default int getLives() {
        return getDamageReceiverSupport().getLives();
    }

    /**
     * Returns the time in seconds since this Item has been hit the last time,
     * or a negative value if it has never been hit.
     */
    default float getTimeSinceLastHit() {
        return getDamageReceiverSupport().getTimeSinceLastHit();
    }

    /**
     * This method is called whenever the item is hit. This  method reduces the number of lives and
     * destroys it (by calling {@linkplain Item#destroy()}) if there are no lives left.
     *
     * @param item the item hitting this item
     */
    default void hitBy(Item item) {
        getDamageReceiverSupport().hitBy(item);
    }
}
