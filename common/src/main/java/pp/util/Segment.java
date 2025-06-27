//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * A directed straight line segment between two positions in the plane.
 *
 * @param from the start position of the segment
 * @param to   the end position of the segment
 */
public record Segment(Position from, Position to) implements SegmentLike {}
