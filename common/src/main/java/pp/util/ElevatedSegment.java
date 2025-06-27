//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * A segment where both end points have elevation information.
 */
public record ElevatedSegment(ElevatedPoint from, ElevatedPoint to) implements SegmentLike {}
