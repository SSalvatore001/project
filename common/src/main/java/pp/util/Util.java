//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class with auxiliary functions.
 */
public class Util {
    private Util() { /* do not instantiate */ }

    /**
     * Calculates and returns the area of a polygon defined by a list of points, using the shoelace formula.
     * The result is positive if the sequence of vertices is in clockwise order, and negative
     * otherwise.
     *
     * @param points A list of positions that define the vertices of the polygon in planar coordinates.
     * @return The calculated area of the polygon.
     */
    public static float getArea(List<? extends Position> points) {
        float sum = 0;
        Position prev = getLast(points);
        for (var next : points) {
            sum += prev.getX() * next.getY() - next.getX() * prev.getY();
            prev = next;
        }
        return 0.5f * sum;
    }

    /**
     * Returns the last element of the given list.
     *
     * @param list the list from which to retrieve the last element
     * @param <T>  the type of elements in the list
     * @return the last element of the list
     * @throws IndexOutOfBoundsException if the list is empty
     */
    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

    /**
     * Reverses the order of elements in the given list.
     *
     * @param list the list to be reversed
     * @param <T>  the type of elements in the list
     * @return a new list with elements in reversed order
     */
    public static <T> List<T> reverse(List<T> list) {
        final List<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Creates a copy of the given list.
     *
     * @param list the list to be copied, may be null
     * @param <T>  the type of elements in the list
     * @return a new list containing the elements of the original list, or null if the original list is null
     */
    public static <T> List<T> copy(List<T> list) {
        return list == null ? null : new ArrayList<>(list);
    }

    /**
     * Adds an element to a set and returns a new set containing the original elements and the new element.
     *
     * @param set     the original set, must not be null
     * @param element the element to be added to the set
     * @param <T>     the type of elements in the set
     * @param <E>     the type of the element being added, must extend T
     * @return a new set containing the original elements and the new element
     */
    public static <T, E extends T> Set<T> add(Set<T> set, E element) {
        final Set<T> newSet = new HashSet<>(set);
        newSet.add(element);
        return newSet;
    }
}
