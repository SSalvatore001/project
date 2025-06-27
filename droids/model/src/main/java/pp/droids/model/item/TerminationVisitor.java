//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * A visitor that determines whether a robot (or similar item) has either:
 * <ul>
 *   <li>Fallen off the platform (i.e., left its current polygon ground), or</li>
 *   <li>Reached a finish line.</li>
 * </ul>
 * <p>
 * If a robot is no longer on a platform or overlaps its current platform abnormally,
 * it will be destroyed. If it reaches a finish line, the game model is notified.
 * </p>
 */
class TerminationVisitor extends VoidVisitorAdapter {

    /**
     * The robot or item to test for termination conditions.
     */
    private final Item robot;

    /**
     * Creates a visitor to evaluate termination conditions for the given robot.
     *
     * @param robot the item to be monitored for termination
     */
    public TerminationVisitor(Item robot) {
        this.robot = robot;
    }

    /**
     * Ignores non-polygon items (e.g., projectiles, decorations).
     *
     * @param item the abstract item (not used)
     */
    @Override
    protected void handle(AbstractItem item) {
        // No operation for generic items
    }

    /**
     * Checks whether the robot has left the platform.
     * If the robot is still assigned to this polygon as ground,
     * but no longer overlaps with it, the robot is destroyed.
     *
     * @param poly the polygon to check against
     */
    @Override
    public void visit(Polygon poly) {
        if (robot.getGround() == poly && robot.overlap(robot, poly))
            robot.destroy();
    }

    /**
     * Checks whether the robot has reached a finish line.
     * If so, the game model is informed.
     *
     * @param line the finish line being visited
     */
    @Override
    public void visit(FinishLine line) {
        if (line.contains(robot))
            line.getModel().reachedFinishLine(robot);
    }
}
