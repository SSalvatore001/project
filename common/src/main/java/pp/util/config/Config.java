//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Abstract base class for representing configurations that can be read from properties files.
 * Subclasses can define fields annotated with {@link Property} to specify the configuration keys
 * and optionally {@link Separator} to specify custom separators for array values.
 */
public abstract class Config {
    private static final String BLANK_SEQ = " *";

    /**
     * Annotation for specifying the property key for a field.
     */
    @Retention(RUNTIME)
    @Target(FIELD)
    @Documented
    protected @interface Property {
        /**
         * The key of the property.
         */
        String value();
    }

    /**
     * Annotation for specifying a custom separator for array values.
     */
    @Retention(RUNTIME)
    @Target(FIELD)
    @Documented
    protected @interface Separator {
        /**
         * The separator for array values.
         */
        String value();
    }

    private static final Logger LOGGER = System.getLogger(Config.class.getName());
    private static final Map<Class<?>, Function<String, ?>> CONVERTER_MAP = new HashMap<>();

    static {
        CONVERTER_MAP.put(String.class, x -> x);
        CONVERTER_MAP.put(byte.class, Byte::parseByte);
        CONVERTER_MAP.put(short.class, Short::parseShort);
        CONVERTER_MAP.put(int.class, Integer::parseInt);
        CONVERTER_MAP.put(long.class, Long::parseLong);
        CONVERTER_MAP.put(boolean.class, Boolean::parseBoolean);
        CONVERTER_MAP.put(float.class, Float::parseFloat);
        CONVERTER_MAP.put(double.class, Double::parseDouble);
        CONVERTER_MAP.put(Byte.class, Byte::parseByte);
        CONVERTER_MAP.put(Short.class, Short::parseShort);
        CONVERTER_MAP.put(Integer.class, Integer::parseInt);
        CONVERTER_MAP.put(Long.class, Long::parseLong);
        CONVERTER_MAP.put(Boolean.class, Boolean::parseBoolean);
        CONVERTER_MAP.put(Float.class, Float::parseFloat);
        CONVERTER_MAP.put(Double.class, Double::parseDouble);
    }

    /**
     * Reads the specified properties file and sets the values of this config using {@link #readFrom(Properties)}.
     *
     * @param file the properties file to read
     * @throws IOException if an I/O error occurs
     * @see #readFrom(Properties)
     */
    public void readFrom(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            final Properties properties = new Properties();
            properties.load(reader);
            readFrom(properties);
        }
    }

    /**
     * Sets the values of fields annotated with {@link Property} using the specified properties.
     * Array fields can be split into components using the default separator (",") or a custom separator
     * specified with {@link Separator}.
     *
     * @param props the properties to read from
     */
    public void readFrom(Properties props) {
        for (Class<?> clazz = getClass(); Config.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                final Property keyAnnot = field.getAnnotation(Property.class);
                if (keyAnnot != null && props.containsKey(keyAnnot.value())) {
                    try {
                        final String text = props.getProperty(keyAnnot.value());
                        final Object value = createValue(text, field);
                        setField(field, value);
                    }
                    catch (IllegalAccessException ex) {
                        LOGGER.log(Level.ERROR, "Cannot access " + field, ex); //NON-NLS
                    }
                }
            }
        }
    }

    /**
     * Reads the specified properties file and sets the values of this config if the file exists,
     * otherwise uses default values. This method is a convenience version of {@link #readFrom(File)}
     * that checks the existence of the specified file and does nothing if the file does not exist.
     *
     * @param file the properties file to read, if it exists
     */
    public void readFromIfExists(File file) {
        if (!file.exists()) {
            LOGGER.log(Level.INFO, "There is no config file {0}; using default configuration", //NON-NLS
                       file.getAbsolutePath());
            return;
        }
        try {
            readFrom(file);
            LOGGER.log(Level.INFO, "Successfully read config from {0}", file.getAbsolutePath()); //NON-NLS
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot read config file " + file.getAbsolutePath(), e); //NON-NLS
        }
    }

    /**
     * Converts a string value from the properties file into an object that can be assigned to the specified field.
     * For array fields, the string value is first split into components.
     *
     * @param value the string value to convert
     * @param field the field to set with the converted value
     * @return an object of the appropriate type for the field
     */
    private Object createValue(String value, Field field) {
        if (!field.getType().isArray())
            return convertToType(value, field.getType());
        // the field is an array
        final Separator sepAnn = field.getDeclaredAnnotation(Separator.class);
        final String sep = sepAnn == null ? "," : sepAnn.value();
        final String[] split = value.split(BLANK_SEQ + sep + BLANK_SEQ, -1);
        final Object array = Array.newInstance(field.getType().componentType(), split.length);
        for (int i = 0; i < split.length; i++)
            Array.set(array, i, convertToType(split[i], field.getType().componentType()));
        return array;
    }

    /**
     * Converts a string value into an object of the specified type.
     *
     * @param value      the string value to convert
     * @param targetType the target type to convert to
     * @return an object of the specified type
     */
    protected Object convertToType(String value, Class<?> targetType) {
        Function<String, ?> handler = CONVERTER_MAP.get(targetType);
        if (handler != null)
            return handler.apply(value);
        throw new IllegalArgumentException("Cannot translate " + value + " to " + targetType);
    }

    /**
     * Returns a string representation of the configuration object, including all properties and their values.
     *
     * @return a string representation of the configuration object
     */
    @Override
    public String toString() {
        final List<String> propertyStrings = getPropertyStrings();
        propertyStrings.sort(String.CASE_INSENSITIVE_ORDER);
        return "[\n" + String.join(",\n", propertyStrings) + "\n]";
    }

    /**
     * Retrieves all property strings of the configuration object.
     *
     * @return a list of property strings
     */
    private List<String> getPropertyStrings() {
        final List<String> propertyStrings = new ArrayList<>();
        for (Class<?> clazz = getClass(); Config.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                final String stringRepresentation = getStringRepresentation(field);
                if (stringRepresentation != null)
                    propertyStrings.add(stringRepresentation);
            }
        }
        return propertyStrings;
    }

    /**
     * Retrieves the string representation of a field annotated with {@link Property}.
     *
     * @param field the field to retrieve the string representation for
     * @return the string representation of the field, or null if the field is not annotated with {@link Property}
     */
    private String getStringRepresentation(Field field) {
        final Property keyAnnotation = field.getAnnotation(Property.class);
        if (keyAnnotation != null) {
            try {
                final Object fieldValue = getField(field);
                final String valueString = asString(fieldValue);
                return keyAnnotation.value() + " -> " + field.getName() + " = " + valueString;
            }
            catch (IllegalAccessException e) {
                LOGGER.log(Level.ERROR, "Cannot access " + field, e); //NON-NLS
            }
        }
        return null;
    }

    /**
     * Converts an object to its string representation. For arrays, string representations of their components are produced.
     *
     * @param value the object to convert
     * @return the string representation of the object
     */
    private String asString(Object value) {
        if (value == null)
            return "null"; //NON-NLS
        if (!value.getClass().isArray())
            return value.toString();
        final int length = Array.getLength(value);
        final List<String> components = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final Object component = Array.get(value, i);
            components.add(asString(component));
        }
        return "{" + String.join(", ", components) + "}";
    }

    /**
     * Sets the value of a field, making it accessible if necessary.
     *
     * @param field the field to set
     * @param value the value to set
     * @throws IllegalAccessException if the field cannot be accessed
     */
    private void setField(Field field, Object value) throws IllegalAccessException {
        boolean inaccessible = !field.canAccess(this);
        if (inaccessible)
            field.setAccessible(true);
        field.set(this, value);
        if (inaccessible)
            field.setAccessible(false);
        LOGGER.log(Level.TRACE, "Set {0} to {1}", field, value); //NON-NLS
    }

    /**
     * Retrieves the value of a field, making it accessible if necessary.
     *
     * @param field the field to retrieve the value from
     * @return the value of the field
     * @throws IllegalAccessException if the field cannot be accessed
     */
    private Object getField(Field field) throws IllegalAccessException {
        boolean inaccessible = !field.canAccess(this);
        if (inaccessible)
            field.setAccessible(true);
        final Object value = field.get(this);
        if (inaccessible)
            field.setAccessible(false);
        return value;
    }
}
