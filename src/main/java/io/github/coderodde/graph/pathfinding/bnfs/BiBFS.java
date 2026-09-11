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
    
    /**
     * Searches for the shortest path with no memory usage statistics.
     * 
     * @param graph  the graph to search in.
     * @param source the source cell.
     * @param target the target cell.
     * 
     * @return a shortest path if target cell is reachable from the source.
     */
    public static GridGraphPathData search(GridGraph graph, 
                                           GridGraph.Cell source,
                                           GridGraph.Cell target) {
        return search(graph, source, target, false);
    }
    
    /**
     * Searches for the shortest path with no memory usage statistics.
     * 
     * @param graph            the graph to search in.
     * @param source           the source cell.
     * @param target           the target cell.
     * @param memoryStatistics indicates whether to gather memory usage
     *                         statistics.
     * 
     * @return a shortest path if target cell is reachable from the source and
     *         also the memory usage statistics.
     */
    public static GridGraphPathData search(GridGraph graph, 
                                           GridGraph.Cell source,
                                           GridGraph.Cell target,
                                           boolean memoryStatistics) {
        
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
            
            if (parentsA.size() <= parentsB.size()) {
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
                                       searchMillis,
                                       memoryStatistics);
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
                                       searchMillis,
                                       memoryStatistics);
                }
            }
        }
        
        long duration = System.currentTimeMillis() - t;
        
        return getPathData(List.of(), 
                           frontierA,
                           frontierB,
                           parentsA, 
                           parentsB, 
                           duration,
                           memoryStatistics);
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
                    long searchMillis,
                    boolean memoryStatistics) {
            
        if (!memoryStatistics) {
            return new GridGraphPathData(path, -1L, -1L, -1L);
        }
            
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
