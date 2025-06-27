//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.Serializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Realizes game map serialization and deserialization using JSON.
 */
public class JsonSerializer implements Serializer {
    /**
     * Loads and returns a game map from the specified json stream.
     *
     * @param model  the game model whose map is loaded
     * @param stream json stream representing a droids map
     * @return the loaded map
     * @throws IOException if any IO error occurs.
     */
    @Override
    public DroidsMap loadMap(InputStream stream, DroidsModel model) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(stream, DroidsMapDTO.class).toMap(model);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Saves a game map to a file.
     *
     * @param map  the game map that is saved
     * @param file file where the game map is written as a json file.
     * @throws IOException if any IO error occurs.
     */
    @Override
    public void saveMap(DroidsMap map, File file) throws IOException {
        new DroidsMapDTO(map).writeToFile(file);
    }
}
