//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import pp.droids.model.item.Item;
import pp.droids.model.item.Robot;
import pp.droids.notifications.ItemAddedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents the entire game map. The game map must not contain more than one droid. The droid may be an
 * inactive member of this map. This means that it is not a member in the list of all items and, therefore,
 * does not take part in any game action; it is then merely an observer. Even though, such an inactive
 * droid may be moved over the map in order to change the view point.
 */
public class DroidsMap {
    private final MapType mapType;

    /**
     * The droid of this droids map.
     */
    private Robot droid;

    /**
     * A list of all items contained in this droids map.
     */
    private final List<Item> items = new ArrayList<>();

    /**
     * Creates an empty map with the specified map type.
     *
     * @param mapType the type of the map
     */
    public DroidsMap(MapType mapType) {
        this.mapType = mapType;
    }

    /**
     * Creates an empty map with the default map type.
     *
     * @see MapType#defaultValue()
     */
    public DroidsMap() {
        this(MapType.defaultValue());
    }

    public MapType getMapType() {
        return mapType;
    }

    /**
     * Returns the list of all items (and all levels) of this map.
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Returns the unique droid of this map, or null if there is no droid.
     *
     * @return droid or null
     */
    public Robot getDroid() {
        return droid;
    }

    /**
     * Called once per frame. This method calls the update method of each item in this map and removes items that
     * cease to exist.
     *
     * @param deltaTime time in seconds since the last update call
     */
    public void update(float deltaTime) {
        // save the reference to the droid just in case update-calls change this.droid
        final Robot droidCopy = droid;
        // and the same for the list of items...
        final ArrayList<Item> itemsCopy = new ArrayList<>(items);

        // Update the droid even if it has been destroyed and has
        // been removed from the list of items. That way one
        // can still navigate the camera if the droid has been
        // destroyed.
        if (droid != null)
            droid.update(deltaTime);
        // Update all the other items
        // Iterate through items in reverse order
        // so that projectiles are moved before the shooter
        final ListIterator<Item> it = itemsCopy.listIterator(itemsCopy.size());
        while (it.hasPrevious()) {
            final Item item = it.previous();
            if (item != droidCopy)
                item.update(deltaTime);
        }

        // remove all destroyed items
        items.removeIf(Item::isDestroyed);
    }

    /**
     * adding a droid to the map, if there is no droid
     *
     * @param newDroid Droid that should be placed
     */
    public void setDroid(Robot newDroid) {
        if (droid != null)
            throw new IllegalArgumentException("adding another droid");
        droid = newDroid;
    }

    /**
     * Adds the specified item to the list of items.
     *
     * @param item the item to be added
     * @throws IllegalArgumentException if the specified item is a droid and there is already a droid in this map
     */
    public void add(Item item) {
        items.add(item);
        item.getModel().notifyListeners(new ItemAddedEvent(item, this));
    }

    /**
     * Removes the specified item from the list of items.
     *
     * @param item the item to be removed
     * @return false if the item was in fact not contained by the list of items before calling this method, true otherwise
     */
    public boolean remove(Item item) {
        if (item instanceof Robot)
            this.droid = null;
        return items.remove(item);
    }
}
