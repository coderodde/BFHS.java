package io.github.coderodde.graph.pathfinding.bnfs;

import io.github.coderodde.graph.pathfinding.bnfs.GridGraph.CellCoordinates;

/**
 * This class implements the Manhattan heuristic function.
 */
public final class ManhattanHeuristicFunction implements HeuristicFunction {

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
    public double estimate(CellCoordinates a, CellCoordinates b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
