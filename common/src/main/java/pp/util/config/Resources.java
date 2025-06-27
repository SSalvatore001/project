//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.config;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides access to the global resource bundle.
 * <p>
 * This class allows initialization and lookup of strings from a resource bundle
 * using a global static reference.
 * </p>
 */
public class Resources {
    private static final Logger LOGGER = System.getLogger(Resources.class.getName());

    /**
     * The global resource bundle.
     */
    private static ResourceBundle bundle;

    /**
     * Initializes the global resource bundle using the specified base name.
     *
     * @param bundleName the base name of the resource bundle to be loaded
     * @throws java.util.MissingResourceException if no resource bundle for the specified base name can be found
     */
    public static void initialize(String bundleName) {
        bundle = ResourceBundle.getBundle(bundleName);
    }

    /**
     * Returns the global resource bundle.
     *
     * @return the global resource bundle
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Retrieves a string for the given key from the global resource bundle.
     *
     * @param key the key for the desired string
     * @return the string corresponding to the given key
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws ClassCastException   if the object found for the given key is not a string
     */
    public static String lookup(String key) {
        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException ex) {
            LOGGER.log(Level.ERROR, "Missing resource: " + key, ex);
            return "missing resource " + key;
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Resources() {
        // do not instantiate
    }
}
