//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Item;
import pp.droids.model.item.Polygon;

import java.lang.System.Logger;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for item DTOs
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CircularItemDTO.class, name = "circular"), //NON-NLS
        @JsonSubTypes.Type(value = PolygonDTO.class, name = "polygon"), //NON-NLS
        @JsonSubTypes.Type(value = FinishLineDTO.class, name = "finish-line") //NON-NLS
})
@JsonInclude(Include.NON_DEFAULT)
abstract class ItemDTO {
    static final Logger LOGGER = System.getLogger(ItemDTO.class.getName());

    @JsonProperty
    String id;

    @JsonProperty
    float x;

    @JsonProperty
    float y;

    @JsonProperty
    float angle;

    @JsonProperty
    boolean destroyed;

    @JsonProperty
    String ground;

    /**
     * Default constructor just for Jackson
     */
    ItemDTO() { /* empty */ }

    ItemDTO(Item item, Map<Item, String> idMap) {
        id = idMap.get(item);
        x = item.getX();
        y = item.getY();
        angle = item.getRotation();
        this.destroyed = item.isDestroyed();
        if (item.getGround() != null)
            ground = idMap.get(item.getGround());
    }

    /**
     * Initializes the specified item based on the state of this object.
     */
    void init(Item item) {
        item.setPos(x, y);
        item.setRotation(angle);
        if (destroyed)
            item.destroy();
    }

    /**
     * Creates an item represented by this object.
     *
     * @param model the model
     * @return the new item.
     */
    abstract Item makeItem(DroidsModel model);

    /**
     * Post-processes this DTO after items have been created for all DTOs.
     *
     * @param map    the map assigning a generated item to each DTO
     * @param errors collects all error messages.
     */
    void postProcess(Map<String, Item> map, List<String> errors) {
        if (ground != null) {
            final Item item = map.get(id);
            item.setGround(get(ground, Polygon.class, map));
        }
    }

    /**
     * Returns the value in the specified map under the specified key.
     * The result is cast to the specified type.
     *
     * @param key   the key
     * @param clazz the class object of the required return type
     * @param map   a map
     * @param <T>   the return value
     * @return the value assigned to the specified key, which must not be null.
     * @throws RuntimeException if no value is assigned to the specified key,
     *                          or if the value does not have the specified type.
     */
    static <T> T get(String key, Class<T> clazz, Map<String, Item> map) {
        final Item item = map.get(key);
        if (item == null)
            throw new RuntimeException("cannot find " + clazz.getName() + " with id " + key);
        if (clazz.isInstance(item))
            return clazz.cast(item);
        throw new RuntimeException(key + " should be a " + clazz.getName() + ", but is a " + item.getClass().getName());
    }
}
