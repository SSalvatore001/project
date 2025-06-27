//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.util.Position;
import pp.util.SegmentLike;

import java.util.SortedSet;
import java.util.TreeSet;

import static pp.util.FloatMath.sqr;
import static pp.util.FloatMath.sqrt;

/**
 * Represents a ray cast from a starting position in a given direction,
 * collecting items intersected by the ray sorted by their hit distance.
 * <p>
 * The ray visits circular items and polygonal items, computing the
 * closest intersection point along the ray. Results are available
 * as a sorted set of {@link DistanceItem} by ascending distance.
 * </p>
 */
class Ray implements VoidVisitor {
    /**
     * Small epsilon to filter out numerically unstable intersections.
     */
    private static final float EPS = 1e-6f;

    /**
     * Starting point of the ray.
     */
    private final Position start;

    /**
     * Direction vector components (dx, dy) of the ray.
     */
    private final float dx;
    private final float dy;

    /**
     * Length and squared length of the direction vector.
     */
    private final float len;
    private final float lenSq;

    /**
     * Sorted set of intersected items paired with their hit distances.
     */
    private final TreeSet<DistanceItem> items = new TreeSet<>();

    /**
     * Constructs a new ray from the given start in direction (dx, dy).
     *
     * @param start the origin position of the ray
     * @param dx    the x-component of the ray direction
     * @param dy    the y-component of the ray direction
     */
    public Ray(Position start, float dx, float dy) {
        this.start = start;
        this.dx = dx;
        this.dy = dy;
        this.lenSq = sqr(dx) + sqr(dy);
        this.len = sqrt(lenSq);
    }

    /**
     * Returns a sorted set of items intersected by the ray, each
     * paired with its distance from the start.
     *
     * @return sorted set of {@link DistanceItem}
     */
    public SortedSet<DistanceItem> getItems() {
        return items;
    }

    /**
     * Visits a circular item and computes intersection distance.
     *
     * @param item the circular item to test
     */
    private void handle(CircularItem item) {
        final float x = start.getX() - item.getX();
        final float y = start.getY() - item.getY();
        final float b = x * dx + y * dy;
        final float d = b * b - lenSq * (x * x + y * y - sqr(item.getRadius()));
        if (d < 0f) return;
        final float sqrd = sqrt(d);
        final float distance2 = (sqrd - b) / len;
        if (distance2 <= 0f) return;
        final float distance1 = (-sqrd - b) / len;
        add(distance1 > 0f ? distance1 : distance2, item);
    }

    /**
     * Visits a polygonal item and computes the nearest segment intersection.
     *
     * @param item the polygon item to test
     */
    private void handle(PolygonItem item) {
        float distance = -1f;
        for (SegmentLike segment : item.getAllSegments()) {
            final float d = distanceTo(segment);
            if (Float.isNaN(d) || d < EPS) continue;
            if (distance < 0 || d < distance)
                distance = d;
        }
        if (distance > 0f)
            add(distance, item);
    }

    /**
     * Computes intersection distance from start to a segment if hit, or NaN.
     *
     * @param segment the segment to test
     * @return distance along ray to intersection point, or NaN if none
     */
    private float distanceTo(SegmentLike segment) {
        final float q = segment.quotient(start, dx, dy);
        if (Float.isNaN(q) || q < 0f || q > 1f)
            return Float.NaN;
        final Position p = segment.pointAt(q);
        if ((p.getX() - start.getX()) * dx + (p.getY() - start.getY()) * dy < 0f)
            return Float.NaN;
        return p.distanceTo(start);
    }

    /**
     * Adds an item with its intersection distance to the result set.
     *
     * @param distance the distance from start to intersection
     * @param item     the item intersected
     */
    private void add(float distance, Item item) {
        items.add(new DistanceItem(distance, item));
    }

    @Override
    public void visit(Robot robot) {
        handle(robot);
    }

    @Override
    public void visit(Obstacle obstacle) {
        handle(obstacle);
    }

    @Override
    public void visit(Projectile proj) {
        // ignore projectiles
    }

    @Override
    public void visit(Polygon poly) {
        handle(poly);
    }

    @Override
    public void visit(FinishLine line) {
        handle(line);
    }
}
