//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * An iterator that creates a random permutation of positions (x, y) where x and y are integer values
 * in the range {0, ..., width-1} x {0, ..., height-1}. All permutations are uniformly distributed
 * using the Fisher-Yates shuffle (also known as Knuth shuffle).
 *
 * @param <T> the type of elements returned by this iterator
 */
public class RandomPositionIterator<T> implements Iterator<T> {
    private final Random random = new Random();
    private final int height;
    private final Map<Integer, T> movedMap = new HashMap<>();
    private int remaining;
    private final Creator<T> creator;

    /**
     * Functional interface to create instances of type T.
     *
     * @param <T> the type of elements created by this creator
     */
    public interface Creator<T> {
        T create(int x, int y);
    }

    /**
     * Creates a RandomPositionIterator for Position instances with float coordinates.
     *
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return a RandomPositionIterator for Position instances
     */
    public static RandomPositionIterator<Position> floatPoints(int width, int height) {
        return new RandomPositionIterator<>(FloatPoint::new, width, height);
    }

    /**
     * Creates a new permutation iterator generating a random permutation of positions (x, y)
     * where x and y are integer values in the range {0, ..., width-1} x {0, ..., height-1}.
     *
     * @param creator the creator to create instances of type T
     * @param width   the width of the rectangle
     * @param height  the height of the rectangle
     */
    public RandomPositionIterator(Creator<T> creator, int width, int height) {
        this.height = height;
        this.remaining = width * height;
        this.creator = creator;
    }

    @Override
    public boolean hasNext() {
        return remaining > 0;
    }

    @Override
    public T next() {
        if (hasNext()) {
            final int idx = random.nextInt(remaining--); // note that remaining is decremented
            final T result = getWhere(idx);
            if (idx < remaining)
                movedMap.put(idx, getWhere(remaining));
            movedMap.remove(remaining);
            return result;
        }
        throw new NoSuchElementException();
    }

    private T getWhere(int idx) {
        final T movedWhere = movedMap.get(idx);
        if (movedWhere != null)
            return movedWhere;
        final int x = idx / height;
        final int y = idx % height;
        return creator.create(x, y);
    }
}
