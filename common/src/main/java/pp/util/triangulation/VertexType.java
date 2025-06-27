//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.Position;

import static pp.util.Triangle.isRightTurn;

enum VertexType {
    START, END, REGULAR, SPLIT, MERGE;

    /**
     * Returns the type of second vertex when walking from the first via the second to the third vertex
     * with the polygon always to the left.
     *
     * @param prev   the previous vertex
     * @param vertex the vertex whose type will be returned
     * @param next   the next vertex
     * @return the type of the vertex at the second parameter position
     */
    public static VertexType type(Position prev, Position vertex, Position next) {
        if (prev.compareTo(vertex) < 0) {
            if (vertex.compareTo(next) < 0)
                return REGULAR;
            return isRightTurn(prev, vertex, next) ? MERGE : END;
        }
        if (vertex.compareTo(next) > 0)
            return REGULAR;
        return isRightTurn(prev, vertex, next) ? SPLIT : START;
    }
}
