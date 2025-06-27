//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;
import pp.util.ElevatedTriangle;
import pp.util.Position;

import java.util.SortedSet;
import java.util.function.Predicate;

/**
 * Interface of all items.
 */
public interface Item extends Position {
    /**
     * Returns the game model that contains this item.
     *
     * @return the game model
     */
    DroidsModel getModel();

    /**
     * Moves the item to the specified position.
     *
     * @param x x-coordinate of the new position
     * @param y y-coordinate of the new position
     */
    void setPos(float x, float y);

    /**
     * Checks whether the item can be placed at the specified position.
     * This method does not change the item's state.
     *
     * @param x x-coordinate of the position to be checked
     * @param y y-coordinate of the position to be checked
     * @return true if the item may be placed at the specified position
     */
    boolean canBePlacedAt(float x, float y);

    /**
     * Returns the rotation of the item in radians. 0 means a direction towards the x-axis.
     */
    float getRotation();

    /**
     * Sets the rotation of the item in radians
     *
     * @param rotation in radians
     */
    void setRotation(float rotation);

    /**
     * Indicates that this item has been destroyed.
     */
    void destroy();

    /**
     * Creates and returns a copy of this item by cloning it.
     *
     * @throws RuntimeException if cloning this item fails.
     * @see Object#clone()
     */
    Item copy();

    /**
     * Returns whether this item has been destroyed
     *
     * @return true if the item has been destroyed
     */
    boolean isDestroyed();

    /**
     * Returns the ground on which this item is located.
     */
    Polygon getGround();

    /**
     * Sets the ground on which this item is located.
     */
    void setGround(Polygon poly);

    /**
     * Returns the elevation of this item, computed by the
     * position of this item on the ground.
     */
    float getElevation();

    /**
     * Returns the triangle that contains this item's position,
     * or null if there is no such triangle.
     */
    ElevatedTriangle getTriangle();

    /**
     * Called once per frame. Used for updating this item's position etc.
     *
     * @param delta time in seconds since the last update call
     */
    void update(float delta);

    /**
     * This method is called when this item collided with something.
     */
    void collisionDetected();

    /**
     * Checks whether this item (when virtually moved to the specified position)
     * overlaps with the specified item when this item is moved to the specified
     * position.
     *
     * @param pos   position where this item is (virtually) moved for checking the overlap
     * @param other the other item which is checked for overlap
     * @return true if they overlap.
     */
    boolean overlap(Position pos, Item other);

    /**
     * Checks whether this item overlaps with the specified one
     * when this item is moved from the specified position to
     * the other specified position on a straight line.
     *
     * @param from  start position of the straight movement
     * @param to    target position of the straight movement
     * @param other the other item which is checked for overlap
     * @return true if they overlap.
     */
    boolean overlapWhenMoving(Position from, Position to, Item other);

    /**
     * Checks whether this item (when virtually moved to the specified position)
     * collides with any other item in the same map
     * indicating an invalid position of this item.
     *
     * @param pos position where this item is (virtually) moved for checking collisions
     * @return true, if a collision happens
     */
    boolean collisionWithAnyOtherItem(Position pos);

    /**
     * Checks whether this item overlaps with any other item (when virtually
     * moved to the specified position)in the same map
     * and that satisfies the specified predicate. Overlaps may indicate prohibited
     * collisions, which indicate an invalid position of this item, or permitted
     * overlaps with items.
     *
     * @param pos    position where this item is (virtually) moved for checking collisions
     * @param accept only items accepted by this predicate are considered.
     * @return true, if a collision happens
     */
    boolean overlapWithAnyOtherItem(Position pos, Predicate<Item> accept);

    /**
     * Casts a ray from the position of this item in the given direction,
     * collecting items intersected by the ray sorted by their hit distance.
     * <p>
     * The ray visits all items (excluding this item), computing the closest
     * intersection point along the ray. Results are available as a sorted
     * set of {@link DistanceItem} by ascending distance.
     * </p>
     *
     * @return a sorted set of {@link DistanceItem} objects, sorted by ascending distance.
     */
    SortedSet<DistanceItem> getHits(float dx, float dy);

    /**
     * Accept method of the visitor pattern.
     */
    <T> T accept(Visitor<T> v);

    /**
     * Accept method of the {@link VoidVisitor}.
     */
    void accept(VoidVisitor v);
}
