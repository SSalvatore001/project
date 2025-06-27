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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.MapType;
import pp.droids.model.item.Item;
import pp.droids.model.item.Robot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pp.droids.model.json.ItemDTO.get;

/**
 * DTO class for game maps. It is used for writing or reading JSON files.
 */
@JsonInclude(Include.NON_DEFAULT)
class DroidsMapDTO {
    /**
     * The type of the map.
     */
    @JsonProperty
    String type;

    /**
     * The id of the player's droid
     */
    @JsonProperty
    String droid;

    /**
     * A list of all item DTOs contained in this map DTO.
     */
    @SuppressWarnings("CanBeFinal")
    @JsonProperty
    List<ItemDTO> items = new ArrayList<>();

    /**
     * Default constructor necessary for Jackson use.
     */
    private DroidsMapDTO() { /* empty */ }

    /**
     * Creates a map DTO from the specified game map.
     *
     * @param map a game map
     */
    DroidsMapDTO(DroidsMap map) {
        type = map.getMapType().toString();
        final Model2DTOVisitor visitor = new Model2DTOVisitor(map, this);
        if (map.getDroid() != null)
            droid = visitor.getId(map.getDroid());
        for (Item it : map.getItems())
            it.accept(visitor);
    }

    /**
     * Writes this map DTO into a JSON file.
     *
     * @param file a JSON file
     * @throws IOException if any IO error occurs.
     */
    void writeToFile(File file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(file, this);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * getting the MapType
     *
     * @param errors collects all errors
     * @return MapType
     */
    private MapType getMapType(List<String> errors) {
        try {
            if (type != null)
                return MapType.valueOf(type);
        }
        catch (IllegalArgumentException ex) {
            errors.add("when deserializing type: " + ex.getMessage()); //NON-NLS
        }
        return MapType.defaultValue();
    }

    /**
     * Creates a game map from this map DTO.
     *
     * @param model the game model that will be used for creating the map and all items.
     * @return a game map.
     * @throws IOException if anything goes wrong when creating the game map.
     */
    DroidsMap toMap(DroidsModel model) throws IOException {
        final List<String> errors = new ArrayList<>();
        final DroidsMap map = new DroidsMap(getMapType(errors));
        final Map<String, Item> id2item = new HashMap<>();
        final List<Item> newItems = new ArrayList<>();
        for (ItemDTO itemDTO : items) {
            final Item item = itemDTO.makeItem(model);
            id2item.put(itemDTO.id, item);
            newItems.add(item);
        }
        for (ItemDTO itemDTO : items)
            itemDTO.postProcess(id2item, errors);
        newItems.forEach(map::add);
        if (droid != null) {
            map.setDroid(get(droid, Robot.class, id2item));
        }
        if (errors.isEmpty())
            return map;
        throw new IOException(String.join("\n", errors));
    }

}
