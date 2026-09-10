package io.github.coderodde.graph.pathfinding.bnfs;

/**
 * This interface defines the API for grid graph heuristic functions.
 */
public sealed interface HeuristicFunction permits ManhattanHeuristicFunction {
    
    /**
     * Estimates the distance between {@code a} and {@code b}. This estimate
     * must be <b>optimistic</b>. In other words, it may not overestimate the
     * distance.
     * 
     * @param a the first cell coordinates.
     * @param b the second cell coordinates.
     * 
     * @return the shortest path estimate.
     */
    public double estimate(GridGraph.Cell a, GridGraph.Cell b);
}
