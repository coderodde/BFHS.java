package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.List;

/**
 * This record implements the path data.
 */
public final record GridGraphPathData(List<GridGraph.Cell> path,
                                      long searchMillis,
                                      long memoryBytes,
                                      long gcNanos) {
    
    @Override
    public String toString() {
        return "Path length: %d, duration: %d ms, bytes: %d, GC: %d ns"
                .formatted(path.size(),
                           searchMillis,
                           memoryBytes, 
                           gcNanos);
    }
}
