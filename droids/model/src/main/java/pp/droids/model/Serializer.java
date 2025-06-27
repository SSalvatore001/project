//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Interface for loading and saving game maps.
 */
public interface Serializer {
    /**
     * Returns a Json serializer if it is available on the class path, and a NoopSerializer otherwise.
     */
    static Serializer getInstance() {
        try {
            return (Serializer) Class.forName("pp.droids.model.json.JsonSerializer").getConstructor().newInstance();
        }
        catch (ClassNotFoundException e) {
            return new NoopSerializer();
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads and returns a game map from the specified stream.
     *
     * @param model  the game model whose map is loaded
     * @param stream json stream representing a droids map
     * @return the loaded map
     * @throws IOException if any IO error occurs.
     */
    DroidsMap loadMap(InputStream stream, DroidsModel model) throws IOException;

    /**
     * Saves a game map to a file.
     *
     * @param map  the game map that is saved
     * @param file file where the game map is written as a json file.
     * @throws IOException if any IO error occurs.
     */
    void saveMap(DroidsMap map, File file) throws IOException;
}
