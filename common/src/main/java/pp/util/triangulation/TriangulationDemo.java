//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.Position;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pp.util.FloatPoint.p;

public class TriangulationDemo {
    private List<Position> outer;
    private final List<List<Position>> holes = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length == 1)
            new TriangulationDemo(Integer.parseInt(args[0])).run();
        else
            new TriangulationDemo(14).run();
    }

    private void run() {
        final Polygon p = new Polygon(outer);
        holes.forEach(p::addHole);
        final TriangulationPanel triPanel = new TriangulationPanel(20, 20);
        triPanel.setOuter(outer);
        holes.forEach(triPanel::addHole);
        triPanel.setTriangles(p.triangulate());
        final JFrame frame = new JFrame("Triangulation Demo"); //NON-NLS
        frame.setContentPane(triPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    void setOuter(Position... pointList) {
        outer = Arrays.asList(pointList);
    }

    void addHole(Position... pointList) {
        holes.add(Arrays.asList(pointList));
    }

    private TriangulationDemo(int select) {
        switch (select) {
            case 1:
                setOuter(p(0, 0), p(10, 0), p(10, 10), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 3), p(3, 4));
                break;
            case 2:
                setOuter(p(0, 0), p(10, 0), p(10, 12), p(5, 12), p(5, 10), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 3), p(3, 4));
                break;
            case 3:
                setOuter(p(0, 0), p(10, 0), p(10, 12), p(5, 12), p(5, 9), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 3), p(3, 4));
                break;
            case 4:
                setOuter(p(0, 0), p(10, 0), p(10, 12), p(5, 12), p(5, 9), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 8), p(3, 4));
                break;
            case 5:
                setOuter(p(0, 0), p(1, -1), p(2, 0), p(3, -1), p(10, 0), p(10, 12), p(5, 12), p(5, 9), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 8), p(3, 4));
                break;
            case 6:
                setOuter(p(0, 0), p(1, -1), p(2, 0), p(3, -1), p(10, 0), p(10, 10), p(8, 10), p(8, 12), p(5, 12), p(5, 9), p(0, 10));
                addHole(p(2, 3), p(3, 2), p(4, 8), p(3, 4));
                break;
            case 7:
                setOuter(p(0, 0), p(1, 1), p(6, 0), p(6, 3), p(8, 4), p(8, 10), p(6, 8), p(5, 9), p(4, 7), p(3, 8), p(2, 6), p(0, 10));
                break;
            case 8:
                setOuter(p(0, 0), p(1, 1), p(6, 0), p(6, 3), p(8, 4), p(8, 10), p(6, 8), p(5, 9), p(4, 7), p(3, 8), p(2, 6), p(0, 10));
                addHole(p(2, 4), p(3, 3), p(4, 4), p(3, 5));
                break;
            case 9:
                setOuter(p(0, 0), p(1, 1), p(6, 0), p(6, 2), p(8, 4), p(8, 10), p(6, 8), p(5, 9), p(4, 7), p(3, 8), p(2, 6), p(0, 10));
                addHole(p(2, 4), p(3, 3), p(4, 4), p(3, 5));
                break;
            case 10:
                setOuter(p(0, 0), p(10, 0), p(10, 10), p(0, 10));
                break;
            case 11:
                setOuter(p(0, 0), p(10, 0), p(10, 10), p(0, 10));
                addHole(p(1, 1), p(2, 1), p(2, 2), p(1, 2));
                addHole(p(1, 3), p(2, 3), p(2, 4), p(1, 4));
                addHole(p(3, 1), p(4, 1), p(4, 2), p(3, 2));
                addHole(p(3, 3), p(4, 3), p(4, 4), p(3, 4));
                break;
            case 12:
                setOuter(p(0f, 0f), p(30f, 0f), p(30f, 25f), p(0f, 25f));
                addHole(p(10f, 10f), p(10f, 15f), p(20f, 15f), p(20f, 10f));
                break;
            case 13:
                setOuter(p(0f, 0f), p(0, 10), p(1, 9), p(2, 10), p(3, 8), p(4, 10), p(5, 7), p(6, 10), p(7, 6), p(8, 10), p(9, 5), p(10, 10), p(10, 0));
                break;
            case 14:
                setOuter(p(0, 0), p(0, 7), p(1, 5), p(2, 7), p(3, 4), p(4, 7), p(5, 6), p(6, 7), p(6, 0), p(5, 2), p(4, 0), p(3, 3), p(2, 0), p(1, 1));
                break;
            default:
                throw new IllegalArgumentException("select = " + select);
        }
    }
}
