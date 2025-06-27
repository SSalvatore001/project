//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.navigation;

import pp.util.Position;

import java.util.List;

/**
 * Supports searching for an optimal path of a character to another position.
 */
public interface Navigator {
    /**
     * Computes a minimal cost path of the character to the specified position. The path is
     * a list of way points to the target, or an empty list if there is no such path.
     *
     * @return the path from a start position to an end position if it exists,
     * or an empty list if there is no such path.
     */
    List<Position> findPathTo(Position target);
}
