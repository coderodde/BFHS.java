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
public final class AStar {
    
    private AStar() {}
    
    /**
     * Searches for the shortest path with no memory usage statistics.
     * 
     * @param graph  the graph to search in.
     * @param source the source cell.
     * @param target the target cell.
     * @param h      the heuristic function.
     * 
     * @return a shortest path if target cell is reachable from the source.
     */
    public static GridGraphPathData search(GridGraph graph,
                                           GridGraph.Cell source,
                                           GridGraph.Cell target,
                                           GridGraphHeuristicFunction h) {
        
        return search(graph, source, target, h, false);
    }
    
    /**
     * Searches for the shortest path with no memory usage statistics.
     * 
     * @param graph            the graph to search in.
     * @param source           the source cell.
     * @param target           the target cell.
     * @param h                the heuristic function.
     * @param memoryStatistics indicates whether to gather memory usage
     *                         statistics.
     * 
     * @return a shortest path if target cell is reachable from the source and
     *         also the memory usage statistics.
     */
    public static GridGraphPathData search(GridGraph graph, 
                                           GridGraph.Cell source,
                                           GridGraph.Cell target,
                                           GridGraphHeuristicFunction h,
                                           boolean memoryStatistics) {
        
        long t = System.currentTimeMillis();
        
        IntPriorityBinaryHeap<GridGraph.Cell> frontier =
                new IntPriorityBinaryHeap<>();
        
        Map<GridGraph.Cell, GridGraph.Cell> parents = new HashMap<>();
        Map<GridGraph.Cell, Integer> distances      = new HashMap<>();
        
        frontier.insert(source, 0);
        parents.put(source, null);
        distances.put(source, 0);
        
        while (!frontier.isEmpty()) {
            GridGraph.Cell current = frontier.extractTop();
            
            if (current.equals(target)) {
                List<GridGraph.Cell> path = tracebackPath(target, parents);
                
                long searchMillis = System.currentTimeMillis() - t;
                
                return getPathData(path,
                                   frontier, 
                                   parents,
                                   distances,
                                   searchMillis,
                                   memoryStatistics);
            }
            
            List<GridGraph.Cell> successors = current.getNeighbours(graph);
            
            for (GridGraph.Cell successor : successors) {
                if (!parents.containsKey(successor)) {
                     parents.put(successor, current);
                     distances.put(successor, distances.get(current) + 1);
                     frontier.insert(successor,
                                     distances.get(successor) + 
                                     h.estimate(successor, target));
                     
                } else if (distances.get(successor) > 
                           distances.get(current) + 1) {
                    parents.put(successor, current);
                    distances.put(successor, distances.get(current) + 1);
                    frontier.changePriority(successor,
                                            distances.get(successor) + 
                                                    h.estimate(successor, 
                                                               target));
                }
            }        
        }
        
        long searchMillis = System.currentTimeMillis() - t;
        
        return getPathData(List.of(),
                           frontier, 
                           parents,
                           distances,
                           searchMillis,
                           memoryStatistics);
    }
    
    private static GridGraphPathData 
        getPathData(List<GridGraph.Cell>                  path,
                    IntPriorityBinaryHeap<GridGraph.Cell> frontier,
                    Map<GridGraph.Cell, GridGraph.Cell>   parents,
                    Map<GridGraph.Cell, Integer>          distances,
                    long                                  searchMillis,
                    boolean                               memoryStatistics) {
            
        if (!memoryStatistics) {
            return new GridGraphPathData(path, -1L, -1L, -1L, -1L);
        }
            
        long t = System.nanoTime();
        
        System.gc();
        
        long gcNanos = System.nanoTime() - t;
        
        GraphLayout layout = GraphLayout.parseInstance(frontier, parents);
        
        long totalBytes = layout.totalSize();
        long cellCount  = countCells(frontier, parents, distances);
        
        return new GridGraphPathData(path,
                                     searchMillis, 
                                     totalBytes, 
                                     gcNanos,
                                     cellCount);
    }
        
    private static long
         countCells(IntPriorityBinaryHeap<GridGraph.Cell> frontier,
                    Map<GridGraph.Cell, GridGraph.Cell>   parents,
                    Map<GridGraph.Cell, Integer>          distances) {
             
        return frontier.size() + parents.size() + distances.size();
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
