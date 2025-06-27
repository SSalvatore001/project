//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * A simple triangle representation.
 *
 * @param a a triangle point
 * @param b a triangle point
 * @param c a triangle point
 */
public record SimpleTriangle(Position a, Position b, Position c) implements Triangle {}
