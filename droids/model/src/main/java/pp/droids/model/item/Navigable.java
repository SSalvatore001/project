//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.util.navigation.Navigator;

/**
 * Represents an item that can walk and turn with constant speed.
 */
public interface Navigable extends Item {
    /**
     * Returns the speed (in units per second) when the item is walking.
     */
    float getWalkingSpeed();

    /**
     * Returns the angular velocity (in radians per second) when the item is turning.
     */
    float getTurningSpeed();

    /**
     * Returns a navigator that can be used to compute a path for this
     * item to any position.
     *
     * @return a new navigator
     */
    Navigator getNavigator();
}
