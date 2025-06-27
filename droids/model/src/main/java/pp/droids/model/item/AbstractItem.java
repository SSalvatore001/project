//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;
import pp.droids.model.collisions.CollisionPredicate;
import pp.droids.model.collisions.MoveOverlapVisitor;
import pp.droids.model.collisions.OverlapVisitor;
import pp.droids.notifications.ItemDestroyedEvent;
import pp.util.ElevatedTriangle;
import pp.util.FloatPoint;
import pp.util.Position;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.SortedSet;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static pp.util.FloatMath.normalizeAngle;

/**
 * An abstract class representing items.
 */
public abstract class AbstractItem implements Item, Cloneable {
    /**
     * The logging object of an item, primary used for debugging
     */
    protected static final Logger LOGGER = System.getLogger(AbstractItem.class.getName());

    /**
     * The model containing this item.
     */
    protected final DroidsModel model;

    /**
     * Indicates whether the object is destroyed.
     */
    private boolean destroyed = false;
    /**
     * The x-coordinate of a circular item.
     */
    private float x;
    /**
     * The y-coordinate of a circular item.
     */
    private float y;
    /**
     * The rotation of a circular item.
     */
    private float rotation;

    /**
     * The polygon this item is standing on
     */
    private Polygon ground;

    /**
     * The triangle containing this item's position.
     * triangle == null means that the triangle has not yet been
     * checked.
     */
    private ElevatedTriangle triangle;

    private float elevation;

    private boolean elevationComputed;

    /**
     * Creates a new item.
     *
     * @param model the game model containing said item.
     */
    protected AbstractItem(DroidsModel model) {
        this(model, 0f, 0f);
    }

    /**
     * Creates a new item.
     *
     * @param model the game model containing said item.
     */
    protected AbstractItem(DroidsModel model, float x, float y) {
        this.model = requireNonNull(model);
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the game model that contains this item.
     *
     * @return the game model
     */
    @Override
    public DroidsModel getModel() {
        return model;
    }

    /**
     * Moves the item to the specified position.
     *
     * @param x x-coordinate of the new position
     * @param y y-coordinate of the new position
     */
    @Override
    public void setPos(float x, float y) {
        if (x != this.x || y != this.y) {
            this.x = x;
            this.y = y;
            triangle = null;
            elevationComputed = false;
        }
    }

    /**
     * Moves the item to the specified position.
     * Convenience method for {@linkplain #setPos(float, float)}.
     *
     * @param pos the new position
     */
    public void setPos(Position pos) {
        setPos(pos.getX(), pos.getY());
    }

    /**
     * Checks whether the item can be placed at the specified position.
     * This method does not change the item's state.
     *
     * @param x x-coordinate of the position to be checked
     * @param y y-coordinate of the position to be checked
     * @return true if the item may be placed at the specified position
     */
    @Override
    public boolean canBePlacedAt(float x, float y) {
        return !collisionWithAnyOtherItem(new FloatPoint(x, y));
    }

    /**
     * Returns the x-coordinate of the item's center point.
     *
     * @return x-coordinate
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the item's center point.
     *
     * @return y-coordinate
     */
    @Override
    public float getY() {
        return y;
    }

    /**
     * Returns the rotation of the item in radians
     */
    @Override
    public float getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the item in radians
     *
     * @param rotation in radians
     */
    @Override
    public void setRotation(float rotation) {
        this.rotation = normalizeAngle(rotation);
    }

    /**
     * Indicates that this item has been destroyed.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "{0} destroyed", this); //NON-NLS
        destroyed = true;
        model.notifyListeners(new ItemDestroyedEvent(this));
    }

    /**
     * Creates and returns a copy of this item by cloning it.
     *
     * @throws RuntimeException if cloning this item fails.
     * @see #clone()
     */
    @Override
    public Item copy() {
        try {
            return (Item) clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Returns whether this item has been destroyed
     *
     * @return true if the item has been destroyed
     */
    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public Polygon getGround() {
        return ground;
    }

    @Override
    public void setGround(Polygon ground) {
        this.ground = ground;
    }

    @Override
    public float getElevation() {
        if (!elevationComputed) {
            elevationComputed = true;
            elevation = getGround().getElevation(this);
            LOGGER.log(Level.DEBUG, "elevation of {0}: {1}", this, elevation); //NON-NLS
        }
        return elevation;
    }

    /**
     * Returns the triangle that contains this item's position,
     * or null if there is no such triangle.
     */
    @Override
    public ElevatedTriangle getTriangle() {
        if (ground == null)
            return null;
        if (triangle == null)
            triangle = ground.findTriangle(x, y);
        return triangle;
    }

    /**
     * Checks whether this item (when virtually moved to the specified position)
     * overlaps with the specified item when this item is moved to the specified
     * position.
     *
     * @param pos   position where this item is (virtually) moved for checking the overlap
     * @param other the other item which is checked for overlap
     * @return true if they overlap.
     */
    @Override
    public boolean overlap(Position pos, Item other) {
        final Visitor<Boolean> visitor = accept(new OverlapVisitor(pos));
        return visitor != null && other.accept(visitor);
    }

    @Override
    public void update(float delta) {
        // do nothing
    }

    /**
     * This method is called when this item collided with something.
     */
    @Override
    public void collisionDetected() {
        // do nothing
    }

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
    @Override
    public boolean overlapWhenMoving(Position from, Position to, Item other) {
        return other.accept(accept(new MoveOverlapVisitor(from, to)));
    }

    /**
     * Checks whether this item (when virtually moved to the specified position)
     * collides with any other item in the same map
     * indicating an invalid position of this item.
     *
     * @param pos position where this item is (virtually) moved for checking collisions
     * @return true, if a collision happens
     */
    @Override
    public boolean collisionWithAnyOtherItem(Position pos) {
        return overlapWithAnyOtherItem(pos, new CollisionPredicate(this));
    }

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
    @Override
    public boolean overlapWithAnyOtherItem(Position pos, Predicate<Item> accept) {
        for (Item item : model.getDroidsMap().getItems())
            if (accept.test(item) && overlap(pos, item)) return true;
        return false;
    }

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
    @Override
    public SortedSet<DistanceItem> getHits(float dx, float dy) {
        final Ray ray = new Ray(this, dx, dy);
        for (Item item : getModel().getDroidsMap().getItems())
            if (item != this)
                item.accept(ray);
        return ray.getItems();
    }
}
