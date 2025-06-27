//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.util.SegmentLike;

import java.util.List;

public interface PolygonItem extends Item {
    List<? extends SegmentLike> getAllSegments();
}
