//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.util.Objects;

import static pp.util.FloatMath.DEG_TO_RAD;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.RAD_TO_DEG;
import static pp.util.FloatMath.abs;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;
import static pp.util.FloatMath.sqrt;

/**
 * A class for representing angles by unit vectors where angles define
 * polar coordinates.
 */
public class Angle implements Comparable<Angle> {
    /**
     * 0 degrees, i.e., along the x-axis
     */
    public static final Angle ZERO = new Angle(1f, 0f);
    /**
     * 180 degrees, i.e., along the negative x-axis
     */
    public static final Angle PI = new Angle(-1f, 0f);

    /**
     * Returns the minimum of the specified angles with respect to
     * {@linkplain #compareTo(Angle)}.
     *
     * @param u first angle to compare
     * @param v second angle to compare
     */
    public static Angle min(Angle u, Angle v) {
        return u.compareTo(v) <= 0 ? u : v;
    }

    /**
     * The x-coordinate of the unit vector.
     */
    public final float x;
    /**
     * The y-coordinate of the unit vector.
     */
    public final float y;

    /**
     * Creates a new angle represented by the vector with the specified
     * coordinates. Note that the vector must have length 1.
     */
    private Angle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the polar coordinates angle of the specified vector.
     *
     * @param x the vectors x value
     * @param y the vectors y value
     */
    public static Angle fromVector(float x, float y) {
        final float len = sqrt(x * x + y * y);
        if (len < FLT_EPSILON)
            throw new IllegalArgumentException("null vector");
        return new Angle(x / len, y / len);
    }

    /**
     * Returns the polar coordinates angle when looking from the first to the second
     * specified position.
     *
     * @param from first position
     * @param to   second position
     */
    public static Angle direction(Position from, Position to) {
        return fromVector(to.getX() - from.getX(),
                          to.getY() - from.getY());
    }

    /**
     * Returns an Angle object for the specified angle in radians.
     *
     * @param radians the specified radian value
     */
    public static Angle fromRadians(float radians) {
        return new Angle(cos(radians), sin(radians));
    }

    /**
     * Returns an Angle object for the specified angle in degrees.
     *
     * @param degrees the specified degrees value
     */
    public static Angle fromDegrees(float degrees) {
        return fromRadians(degrees * DEG_TO_RAD);
    }

    /**
     * Returns the value of this angle in radians in the range (-pi,pi].
     */
    public float radians() {
        return atan2(y, x);
    }

    /**
     * Returns the value of this angle in degrees in the range (-180,180].
     */
    public float degrees() {
        return radians() * RAD_TO_DEG;
    }

    /**
     * Returns the x-coordinate of the unit vector, that is the cosine of the angle.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the unit vector, that is the sinus of the angle.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the angle obtained by adding the specified angle to this angle.
     *
     * @param o the other angle
     */
    public Angle plus(Angle o) {
        return new Angle(x * o.x - y * o.y,
                         x * o.y + y * o.x);
    }

    /**
     * Returns the angle obtained by subtracting the specified angle from this angle.
     *
     * @param o the other angle
     */
    public Angle minus(Angle o) {
        return new Angle(y * o.y + x * o.x,
                         y * o.x - x * o.y);
    }

    /**
     * Returns the bisector angle between this angle as left angle and the
     * specified right angle, that is, the angle halfway from the right to this
     * angle when turning from right to left.
     *
     * @param right right angle
     * @return the bisector angle between this as left and the specified right angle
     */
    public Angle bisector(Angle right) {
        // compute vector of this.minus(right)
        final float dx = y * right.y + x * right.x;
        final float dy = y * right.x - x * right.y;
        if (abs(dy) < FLT_EPSILON) {
            // the difference is either 0° or 180°
            if (dx > 0f)
                return this;
            else
                return new Angle(-right.y, right.x);
        }
        final float mid = 0.5f * atan2(dy, dx);
        final float sum = right.radians() + mid;
        if (mid > 0f)
            return new Angle(cos(sum), sin(sum));
        else
            return new Angle(-cos(sum), -sin(sum));
    }

    /**
     * Returns true if turning left from the specified angle towards this angle is
     * shorter than turning right towards this angle.
     *
     * @param o another angle
     */
    public boolean leftOf(Angle o) {
        return y * o.x - x * o.y > 0f;
    }

    /**
     * Returns true if turning right from the specified angle towards this angle is
     * shorter than turning left towards this angle.
     *
     * @param o another angle
     */
    public boolean rightOf(Angle o) {
        return y * o.x - x * o.y < 0f;
    }

    /**
     * Compares this angle with the specified one and returns -1, 0, or 1 if
     * the value of this angle is less than, equal to, or greater than the value
     * of the specified angle, respectively, where angle values are in the
     * range [0,2*pi) and 0 means along the x-axis.
     *
     * @param o the other angle
     */
    @Override
    public int compareTo(Angle o) {
        if (y == 0f) {
            if (o.y < 0f)
                return -1;
            if (o.y > 0f)
                return x > 0f ? -1 : 1;
            if (x > 0f)
                return o.x > 0f ? 0 : 1;
            return o.x > 0f ? -1 : 0;
        }
        if (y > 0f) {
            if (o.y < 0f)
                return -1;
            if (o.y == 0f)
                return o.x > 0f ? 1 : -1;
        }
        else if (o.y >= 0f)
            return 1;
        final float det = x * o.y - y * o.x;
        if (det == 0f)
            return 0;
        return det > 0f ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Angle angle)
            return compareTo(angle) == 0;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of this angle in degrees in the range [0,2*pi).
     */
    @Override
    public String toString() {
        if (degrees() < 0f)
            return (degrees() + 360f) + "°";
        return degrees() + "°";
    }
}
