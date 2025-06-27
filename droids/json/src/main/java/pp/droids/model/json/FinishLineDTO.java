//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Item;

import java.util.Map;

/**
 * DTO class for finish lines.
 */
class FinishLineDTO extends ItemDTO {
    /**
     * the x stretch of the finish line.
     */
    @JsonProperty
    float dx;

    /**
     * the y stretch of the finish line.
     */
    @JsonProperty
    float dy;

    /**
     * the height of the polygon walls.
     */
    @JsonProperty
    float height = 0.05f;

    /**
     * Default constructor just for Jackson
     */
    private FinishLineDTO() { /* empty */ }

    /**
     * Creates a new polygon DTO based on an existing polygon.
     *
     * @param item the existing polygon to be represented by the DTO.
     */
    FinishLineDTO(FinishLine item, Map<Item, String> idMap) {
        super(item, idMap);
        this.dx = item.getDx();
        this.dy = item.getDy();
        this.height = item.getElevation();
    }

    /**
     * creates a FinishLine
     *
     * @param model the model
     * @return the created FinishLine
     */
    @Override
    Item makeItem(DroidsModel model) {
        FinishLine line = new FinishLine.Builder().setModel(model)
                                                  .setPos(x, y)
                                                  .setSize(dx, dy)
                                                  .setElevation(height)
                                                  .build();
        if (destroyed)
            line.destroy();
        return line;
    }
}
