package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the breadth-first search.
 */
public final class BFS {
    
    private BFS() {}
    
    public static List<GridGraph.Cell> search(GridGraph graph, 
                                              GridGraph.Cell source,
                                              GridGraph.Cell target) {
        
        Deque<GridGraph.Cell> frontier              = new ArrayDeque<>();
        Map<GridGraph.Cell, GridGraph.Cell> parents = new HashMap<>();
        
        frontier.addLast(source);
        parents.put(source, null);
        
        while (!frontier.isEmpty()) {
            GridGraph.Cell current = frontier.removeFirst();
            
            if (current.equals(target)) {
                return tracebackPath(target, parents);
            }
            
            List<GridGraph.Cell> successors = current.getNeighbours(graph);
            
            for (GridGraph.Cell successor : successors) {
                if (!parents.containsKey(successor)) {
                     parents.put(successor, current);
                     frontier.addLast(successor);
                }
            }        
        }
        
        return List.of();
    }
    
    private static List<GridGraph.Cell> 
        tracebackPath(GridGraph.Cell target,
                      Map<GridGraph.Cell, GridGraph.Cell> parents) {
            
        List<GridGraph.Cell> path = new ArrayList<>();
        GridGraph.Cell current    = target;
        
        while (current != null) {
            path.addLast(current);
            current = parents.get(current);
        }
        
        Collections.reverse(path);
        return path;
    }
}
