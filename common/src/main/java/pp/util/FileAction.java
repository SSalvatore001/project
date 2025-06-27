//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.io.File;
import java.io.IOException;

/**
 * Represents an action that can be performed on a file.
 * <p>
 * This functional interface is intended to be used as the assignment target for lambda
 * expressions or method references. Implementations of this interface define a specific
 * file operation which may throw an {@link IOException} if an I/O error occurs during
 * execution.
 * </p>
 *
 * @see java.io.IOException
 */
@FunctionalInterface
public interface FileAction {
    /**
     * Executes the defined file action on the specified file.
     *
     * @param file the file to be processed
     * @throws IOException if an I/O error occurs during the execution of the file action
     */
    void run(File file) throws IOException;
}
