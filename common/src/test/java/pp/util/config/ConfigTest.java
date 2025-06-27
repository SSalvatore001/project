//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTest {

    private TestConfig config;

    @BeforeEach
    public void setUp() {
        config = new TestConfig();
    }

    @Test
    public void testReadFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("test.string", "hello"); //NON-NLS
        properties.setProperty("test.int", "42");
        properties.setProperty("test.boolean", "true"); //NON-NLS
        properties.setProperty("test.intArray", "1; 2 ;3;4");

        config.readFrom(properties);

        assertEquals("hello", config.getTestString());
        assertEquals(42, config.getTestInt());
        assertTrue(config.isTestBoolean());
        assertArrayEquals(new int[]{1, 2, 3, 4}, config.getTestIntArray());
    }

    @Test
    public void testReadFromFile() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("test.string", "fileTest");
        properties.setProperty("test.int", "24");
        properties.setProperty("test.boolean", "false"); //NON-NLS
        properties.setProperty("test.intArray", "10;20;30");

        File tempFile = File.createTempFile("testConfig", ".properties");
        try (FileWriter writer = new FileWriter(tempFile)) {
            properties.store(writer, null);
        }

        config.readFrom(tempFile);

        assertEquals("fileTest", config.getTestString());
        assertEquals(24, config.getTestInt());
        assertFalse(config.isTestBoolean());
        assertArrayEquals(new int[]{10, 20, 30}, config.getTestIntArray());

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testConvertToType() {
        assertEquals(42, config.convertToType("42", int.class));
        assertEquals(true, config.convertToType("true", boolean.class)); //NON-NLS
        assertEquals(3.14, config.convertToType("3.14", double.class));
    }

    @Test
    public void testConvertToTypeWithUnsupportedType() {
        assertThrows(IllegalArgumentException.class,
                     () -> config.convertToType("unsupported", Object.class)); //NON-NLS
    }

    @Test
    public void testToString() {
        Properties properties = new Properties();
        properties.setProperty("test.string", "stringValue");
        properties.setProperty("test.int", "123");
        properties.setProperty("test.boolean", "true"); //NON-NLS
        properties.setProperty("test.intArray", "5;6;7");

        config.readFrom(properties);

        String expected = "[\ntest.boolean -> testBoolean = true,\n" +
                          "test.int -> testInt = 123,\n" +
                          "test.intArray -> testIntArray = {5, 6, 7},\n" +
                          "test.string -> testString = stringValue\n" +
                          "]";
        assertEquals(expected, config.toString());
    }
}
