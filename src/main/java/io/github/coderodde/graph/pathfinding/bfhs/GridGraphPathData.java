package io.github.coderodde.graph.pathfinding.bfhs;

import java.util.List;

/**
 * This record implements the path data.
 */
public final record GridGraphPathData(List<GridGraph.Cell> path,
                                      long searchMillis,
                                      long memoryBytes,
                                      long gcNanos,
                                      long cellCount) {
    
    @Override
    public String toString() {
        return ("Path length: %d, search duration: %d ms, bytes: %d, " +
                "GC: %d ns, cell count: %d")
                .formatted(path.size(),
                           searchMillis,
                           memoryBytes, 
                           gcNanos,
                           cellCount);
    }
}
