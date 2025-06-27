//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * Pairs an {@link Item} with its distance from a reference point and
 * provides natural ordering by distance.
 *
 * <p>This record is useful for sorting items by proximity, for example
 * when selecting the nearest target or obstacle.</p>
 *
 * @param distance the distance value, in the same units as the positions
 * @param item     the item associated with this distance
 */
public record DistanceItem(float distance, Item item) implements Comparable<DistanceItem> {

    /**
     * Compares this {@code DistancItem} with another by their distance values.
     *
     * @param other the other {@code DistancItem} to compare against
     * @return a negative integer, zero, or a positive integer as this distance
     * is less than, equal to, or greater than the other's distance
     */
    @Override
    public int compareTo(DistanceItem other) {
        return Float.compare(distance, other.distance);
    }
}
