//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.CircularItem;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Robot;

import java.lang.System.Logger.Level;
import java.util.Map;

/**
 * DTO class for circular items.
 */
class CircularItemDTO extends ItemDTO {
    /**
     * The specific kind of item.
     */
    enum ItemKind {
        ROBOT, OBSTACLE
    }

    /**
     * The specific kind of item represented by this DTO.
     */
    @JsonProperty
    ItemKind kind;

    /**
     * The radius of the item.
     */
    @JsonProperty
    float radius;

    /**
     * the number of remaining lives of the item.
     */
    @JsonProperty
    int lives;

    /**
     * the reloading time of the shooter.
     */
    @JsonProperty
    float reloadTime;

    /**
     * Default constructor just for Jackson
     */
    CircularItemDTO() { /* empty */ }

    /**
     * Creates a new DTO based on an item.
     */
    CircularItemDTO(Obstacle item, Map<Item, String> idMap) {
        super(item, idMap);
        this.radius = item.getRadius();
        this.kind = ItemKind.OBSTACLE;
        if (item.getGround() == null)
            LOGGER.log(Level.WARNING, "{0} doesn't have a ground", item); //NON-NLS
    }

    CircularItemDTO(Robot item, Map<Item, String> idMap) {
        super(item, idMap);
        this.radius = item.getRadius();
        this.kind = ItemKind.ROBOT;
        this.lives = item.getLives();
        this.reloadTime = item.getWeapon().getReloadTime();
        if (item.getGround() == null)
            LOGGER.log(Level.WARNING, "{0} doesn't have a ground", item); //NON-NLS
    }

    @Override
    CircularItem makeItem(DroidsModel model) {
        if (ground == null)
            LOGGER.log(Level.WARNING, "item {1} {0} doesn't have a ground", id, kind); //NON-NLS
        final CircularItem item = switch (kind) {
            case OBSTACLE -> new Obstacle(model, radius);
            case ROBOT -> new Robot(model, radius, lives, reloadTime);
        };
        init(item);
        return item;
    }
}

