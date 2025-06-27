//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

public abstract class Item {
    private final String id;

    protected Item(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String detail();

    @Override
    public String toString() {
        return id;
    }
}
