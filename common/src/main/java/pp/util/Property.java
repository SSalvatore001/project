//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * Represents a named property that holds a value.
 * <p>
 * This interface defines methods for retrieving the property's name and value,
 * as well as updating its value.
 * </p>
 *
 * @param <T> the type of the property's value
 */
public interface Property<T> {
    /**
     * Returns the name of this property.
     *
     * @return the property's name
     */
    String getName();

    /**
     * Retrieves the current value of this property.
     *
     * @return the current value of the property
     */
    T getValue();

    /**
     * Sets the value of this property.
     *
     * @param value the new value to be set
     */
    void setValue(T value);
}
