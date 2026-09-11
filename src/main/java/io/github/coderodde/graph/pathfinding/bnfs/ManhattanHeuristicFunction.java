package io.github.coderodde.graph.pathfinding.bnfs;

/**
 * This class implements the Manhattan heuristic function.
 */
public final class ManhattanHeuristicFunction implements GridGraphHeuristicFunction {

    /**
     * Estimates the distance between {@code a} and {@code b} using Manhattan
     * distance.
     * 
     * @param a the first cell coordinates.
     * @param b the second cell coordinates.
     * 
     * @return the Manhattan distance between the two cells. 
     */
    @Override
    public double estimate(GridGraph.Cell a, GridGraph.Cell b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
