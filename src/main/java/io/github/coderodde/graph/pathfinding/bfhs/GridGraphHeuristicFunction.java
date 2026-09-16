package io.github.coderodde.graph.pathfinding.bfhs;

/**
 * This interface defines the API for grid graph heuristic functions.
 */
public sealed interface GridGraphHeuristicFunction 
        permits ManhattanHeuristicFunction {
    
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
    public int estimate(GridGraph.Cell a, GridGraph.Cell b);
}
