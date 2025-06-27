//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomPositionIteratorTest {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 25;

    @Test
    public void permutation() {
        for (int i = 0; i < 10; i++) {
            final List<Position> permutation = new ArrayList<>();
            RandomPositionIterator.floatPoints(WIDTH, HEIGHT).forEachRemaining(permutation::add);
            assertEquals(WIDTH * HEIGHT, permutation.size());
            assertEquals(permutation.size(), new HashSet<>(permutation).size());
            for (Position w : permutation) {
                assertTrue(0 <= w.getX() && w.getX() < WIDTH, w.toString());
                assertTrue(0 <= w.getY() && w.getY() < HEIGHT, w.toString());
            }
        }
    }
}