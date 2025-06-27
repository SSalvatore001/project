//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatPoint.p;

public class TriangleTest {
    @Test
    public void triangle1() {
        final Triangle t = new SimpleTriangle(p(1, 1), p(4, 2), p(3, 3));
        assertFalse(t.turnsLeft());

        assertTrue(t.contains(3, 2, 0));
        assertTrue(t.contains(3.5f, 2.5f, FLT_EPSILON));
        assertTrue(t.contains(2.5f, 1.5f, FLT_EPSILON));
        assertTrue(t.contains(2.0f, 2.0f, FLT_EPSILON));

        assertFalse(t.contains(3.5f, 2.6f, FLT_EPSILON));
        assertFalse(t.contains(2.5f, 1.4f, FLT_EPSILON));
        assertFalse(t.contains(2.0f, 2.1f, FLT_EPSILON));
    }

    @Test
    public void triangle2() {
        final Triangle t = new SimpleTriangle(p(1, 1), p(3, 3), p(4, 2));
        assertTrue(t.turnsLeft());

        assertTrue(t.contains(3, 2, 0));
        assertTrue(t.contains(3.5f, 2.5f, FLT_EPSILON));
        assertTrue(t.contains(2.5f, 1.5f, FLT_EPSILON));
        assertTrue(t.contains(2.0f, 2.0f, FLT_EPSILON));

        assertFalse(t.contains(3.5f, 2.6f, FLT_EPSILON));
        assertFalse(t.contains(2.5f, 1.4f, FLT_EPSILON));
        assertFalse(t.contains(2.0f, 2.1f, FLT_EPSILON));
    }
}