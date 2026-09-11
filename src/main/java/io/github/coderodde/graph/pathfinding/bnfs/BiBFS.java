package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openjdk.jol.info.GraphLayout;

/**
 * This class implements the bidirectional breadth-first search.
 */
public final class BiBFS {
    
    private BiBFS() {}
    
    public static GridGraphPathData search(GridGraph graph, 
                                           GridGraph.Cell source,
                                           GridGraph.Cell target) {
        
        long t = System.currentTimeMillis();
        
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
                    List<GridGraph.Cell> path = tracebackPath(current,
                                                              parentsA, 
                                                              parentsB);
                    
                    long searchMillis = System.currentTimeMillis() - t;
                    
                    return getPathData(path,
                                       frontierA,
                                       frontierB, 
                                       parentsA, 
                                       parentsB, 
                                       searchMillis);
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
                    List<GridGraph.Cell> path = tracebackPath(current,
                                                              parentsA, 
                                                              parentsB);
                    
                    long searchMillis = System.currentTimeMillis() - t;
                    
                    return getPathData(path,
                                       frontierA,
                                       frontierB, 
                                       parentsA, 
                                       parentsB, 
                                       searchMillis);
                }
            }
        }
        
        long duration = System.currentTimeMillis() - t;
        
        return getPathData(List.of(), 
                           frontierA,
                           frontierB,
                           parentsA, 
                           parentsB, 
                           duration);
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
        
    private static GridGraphPathData
        getPathData(List<GridGraph.Cell> path,
                    Deque<GridGraph.Cell> frontierA,
                    Deque<GridGraph.Cell> frontierB,
                    Map<GridGraph.Cell, GridGraph.Cell> parentsA,
                    Map<GridGraph.Cell, GridGraph.Cell> parentsB,
                    long searchMillis) {
            
        long t = System.nanoTime();
        
        System.gc();
        
        long gcNanos = System.nanoTime() - t;
        
        GraphLayout layout = GraphLayout.parseInstance(frontierA,
                                                       frontierB,
                                                       parentsA,
                                                       parentsB);
        
        long totalBytes = layout.totalSize();
        
        return new GridGraphPathData(path, 
                                     searchMillis, 
                                     totalBytes, 
                                     gcNanos);
    }
}
