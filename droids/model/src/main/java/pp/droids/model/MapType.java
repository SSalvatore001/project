//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

/**
 * Enumeration type of the different types of DroidsMap.
 */
public enum MapType {

    /**
     * The DroidsMap type with a castle look.
     */
    CASTLE,

    /**
     * The DroidsMap type with a look inspired by ancient Egypt.
     */
    EGYPT;

    /**
     * Returns the default DroidsMap type, which is just the first value of this enumeration type.
     */
    public static MapType defaultValue() {
        return values()[0];
    }

    /**
     * Returns the name specifying wall types.
     */
    public String wallSpec() {
        return switch (this) {
            case EGYPT -> Spec.HIEROGLYPHS;
            case CASTLE -> Spec.WALL;
        };
    }

    /**
     * Returns the name specifying platform types.
     */
    public String platformSpec() {
        return switch (this) {
            case EGYPT -> Spec.SAND;
            case CASTLE -> Spec.STONE;
        };
    }
}
