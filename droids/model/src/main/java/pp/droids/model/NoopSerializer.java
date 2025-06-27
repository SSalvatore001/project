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

/**
 * A serializer that does not implement saving or loading,
 * but just throws IOIExceptions.
 */
class NoopSerializer implements Serializer {
    @Override
    public DroidsMap loadMap(InputStream stream, DroidsModel model) throws IOException {
        throw new IOException("loading of maps is not supported");
    }

    @Override
    public void saveMap(DroidsMap map, File file) throws IOException {
        throw new IOException("saving maps is not supported");
    }
}
