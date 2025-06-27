//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pp.util.FloatMath.ZERO_TOLERANCE;

public class IntervalTest {
    private Interval interval;

    @BeforeEach
    public void setUp() {
        interval = new Interval(0f, 1f);
    }

    @Test
    public void contains() {
        assertTrue(interval.contains(0.5f));
        assertTrue(interval.contains(0f));
        assertTrue(interval.contains(1f));
        assertFalse(interval.contains(1.5f));
        assertFalse(interval.contains(-0.5f));
    }

    @Test
    public void matches() {
        assertTrue(interval.matches(new Interval(0f, 1f), ZERO_TOLERANCE));
        assertFalse(interval.matches(new Interval(0f, 0.99f), ZERO_TOLERANCE));
    }
}