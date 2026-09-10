package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the bidirectional breadth-first search.
 */
public final class BiBFS {
    
    private BiBFS() {}
    
    public static List<GridGraph.Cell> search(GridGraph graph, 
                                              GridGraph.Cell source,
                                              GridGraph.Cell target) {
        
        Deque<GridGraph.Cell> frontierA              = new ArrayDeque<>();
        Deque<GridGraph.Cell> frontierB              = new ArrayDeque<>();
        Map<GridGraph.Cell, GridGraph.Cell> parentsA = new HashMap<>();
        Map<GridGraph.Cell, GridGraph.Cell> parentsB = new HashMap<>();
        
        frontierA.addLast(source);
        frontierB.addLast(target);
        
        parentsA.put(source, null);
        parentsB.put(target, null);
        
        while (!frontierA.isEmpty() && !frontierB.isEmpty()) {
            int traversedA = frontierA.size() + parentsA.size();
            int traversedB = frontierB.size() + parentsB.size();
            
            if (traversedA <= traversedB) {
                GridGraph.Cell current = frontierA.removeFirst();

                List<GridGraph.Cell> successors = current.getNeighbours(graph);
                
                for (GridGraph.Cell successor : successors) {
                    if (!parentsA.containsKey(successor)) {
                         parentsA.put(successor, current);
                         frontierA.addLast(successor);
                    }
                }
                
                if (parentsB.containsKey(current)) {
                    return tracebackPath(current,
                                         parentsA, 
                                         parentsB);
                }
            } else {
                GridGraph.Cell current = frontierB.removeFirst();

                List<GridGraph.Cell> successors = current.getNeighbours(graph);

                for (GridGraph.Cell successor : successors) {
                    if (!parentsB.containsKey(successor)) {
                         parentsB.put(successor, current);
                         frontierB.addLast(successor);
                    }
                }

                if (parentsA.containsKey(current)) {
                    return tracebackPath(current,
                                         parentsA, 
                                         parentsB);
                }
            }
        }
        
        return List.of();
    }
    
    private static List<GridGraph.Cell>
        tracebackPath(GridGraph.Cell middle,
                      Map<GridGraph.Cell, GridGraph.Cell> parentsA,
                      Map<GridGraph.Cell, GridGraph.Cell> parentsB) {
            
        List<GridGraph.Cell> path = new ArrayList<>();
        GridGraph.Cell current    = middle;
        
        while (current != null) {
            path.addLast(current);
            current = parentsA.get(current);
        }
        
        Collections.reverse(path);
        
        current = parentsB.get(middle);
        
        while (current != null) {
            path.addLast(current);
            current = parentsB.get(current);
        }
        
        return path;
    }
}
