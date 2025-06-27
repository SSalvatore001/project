//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.config;

class TestConfig extends Config {
    @Property("test.string")
    private String testString;

    @Property("test.int")
    private int testInt;

    @Property("test.boolean")
    private boolean testBoolean;

    @Property("test.intArray")
    @Separator(";")
    private int[] testIntArray;

    // Getters for testing
    public String getTestString() {
        return testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public boolean isTestBoolean() {
        return testBoolean;
    }

    public int[] getTestIntArray() {
        return testIntArray;
    }
}
