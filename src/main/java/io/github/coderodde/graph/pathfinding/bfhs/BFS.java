package io.github.coderodde.graph.pathfinding.bfhs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openjdk.jol.info.GraphLayout;

/**
 * This class implements the breadth-first search.
 */
public final class BFS {
    
    private BFS() {}
    
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
        
        Deque<GridGraph.Cell> frontier              = new ArrayDeque<>();
        Map<GridGraph.Cell, GridGraph.Cell> parents = new HashMap<>();
        
        frontier.addLast(source);
        parents.put(source, null);
        
        while (!frontier.isEmpty()) {
            GridGraph.Cell current = frontier.removeFirst();
            
            if (current.equals(target)) {
                List<GridGraph.Cell> path = tracebackPath(target, parents);
                
                long searchMillis = System.currentTimeMillis() - t;
                
                return getPathData(path,
                                   frontier, 
                                   parents,
                                   searchMillis,
                                   memoryStatistics);
            }
            
            List<GridGraph.Cell> successors = current.getNeighbours(graph);
            
            for (GridGraph.Cell successor : successors) {
                if (!parents.containsKey(successor)) {
                     parents.put(successor, current);
                     frontier.addLast(successor);
                }
            }        
        }
        
        long searchMillis = System.currentTimeMillis() - t;
        
        return getPathData(List.of(),
                           frontier, 
                           parents, 
                           searchMillis,
                           memoryStatistics);
    }
    
    private static GridGraphPathData 
        getPathData(List<GridGraph.Cell> path,
                    Deque<GridGraph.Cell> frontier,
                    Map<GridGraph.Cell, GridGraph.Cell> parents,
                    long searchMillis,
                    boolean memoryStatistics) {
            
        if (!memoryStatistics) {
            return new GridGraphPathData(path, -1L, -1L, -1L, -1L);
        }
            
        long t = System.nanoTime();
        
        System.gc();
        
        long gcNanos = System.nanoTime() - t;
        
        GraphLayout layout = GraphLayout.parseInstance(frontier, parents);
        
        long totalBytes = layout.totalSize();
        long cellCount  = countCells(frontier, parents);
        
        return new GridGraphPathData(path,
                                     searchMillis, 
                                     totalBytes, 
                                     gcNanos,
                                     cellCount);
    }
        
    private static long
         countCells(Deque<GridGraph.Cell> frontier,
                    Map<GridGraph.Cell, GridGraph.Cell> parents) {
        return frontier.size() + parents.size();
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
