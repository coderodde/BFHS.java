package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openjdk.jol.info.GraphLayout;

/**
 * This class implements the BFHS (Breadth-first heuristic search) algorithm
 * discussed in the paper
 * <a href="https://www.sciencedirect.com/science/article/pii/S0004370205002158">Breadth-first heuristic search</a>.
 */
public final class BFHS {

    private BFHS() {}
    
    
    
    public static GridGraphPathData search(GridGraph graph,
                                           GridGraph.Cell source,
                                           GridGraph.Cell target,
                                           GridGraphHeuristicFunction h,
                                           int u) {
        return search(graph,
                      source,
                      target,
                      h,
                      u,
                      false);
    }
    
    public static GridGraphPathData search(GridGraph graph,
                                           GridGraph.Cell source,
                                           GridGraph.Cell target,
                                           GridGraphHeuristicFunction h,
                                           int u,
                                           boolean memoryStatistics) {
        return search(graph,
                      source,
                      target,
                      h,
                      u,
                      0,
                      memoryStatistics);
    }
    
    private static GridGraphPathData search(GridGraph graph,
                                            GridGraph.Cell source,
                                            GridGraph.Cell target,
                                            GridGraphHeuristicFunction h,
                                            int u,
                                            int depth,
                                            boolean memoryStatistics)  {
        
        if (source.equals(target)) {
            return new GridGraphPathData(List.of(source), 0L, 0L, 0L);
        }
        
        checkU(u);
        
        long t = System.currentTimeMillis();
        
        Map<GridGraph.Cell, Integer> g                = new HashMap<>();
        Map<GridGraph.Cell, GridGraph.Cell> ancestors = new HashMap<>();
        
        List<DoublePriorityBinaryHeap<GridGraph.Cell>> open = 
            new ArrayList<>();
        
        List<Set<GridGraph.Cell>> closed = new ArrayList<>();
        
        open.addLast(new DoublePriorityBinaryHeap<>());
        open.addLast(new DoublePriorityBinaryHeap<>());
        closed.addLast(new HashSet<>());
        
        open.get(0).insert(source, 0);
        
        g.put(source, 0);
        ancestors.put(source, null);
        
        int l = 0;
        int relay = u / 2;
        
        while (!open.get(l).isEmpty() || !open.get(l + 1).isEmpty()) {
            while (!open.get(l).isEmpty()) {
                GridGraph.Cell n = open.get(l).extractTop();
                
                closed.get(l).add(n);
                
                GridGraph.Cell sol = 
                    expandNode(
                        graph,     // The owner graph.
                        n,         // Node to expand.
                        source,    // The source node
                        target,    // The target node.e
                        l,         // The level/depth counter.
                        relay,     // The index of the relay layer.
                        u,         // The upper bound on the path.
                        open,      // The open set stack.
                        closed,    // The closed set stack.
                        g,         // Maps each node to cost.
                        ancestors, // Maps each node to ancestor.
                        h);        // The heuristic function.
                
                if (sol != null) {
                    GridGraphPathData pathData0;
                    GridGraphPathData pathData1;

                    GridGraph.Cell middle = ancestors.get(sol);
                    
                    if (g.get(middle) == 1) {
                        pathData0 = new GridGraphPathData(
                                            List.of(source, middle), 
                                            0L, 
                                            0L, 
                                            0L);
                    } else {
                        pathData0 = search(graph, 
                                           source,
                                           middle, 
                                           h, 
                                           g.get(middle),
                                           depth + 1,
                                           memoryStatistics);
                    }
                    
                    if (g.get(sol) - g.get(middle) == 1) {
                        pathData1 = new GridGraphPathData(List.of(middle, sol),
                                                          0L,
                                                          0L,
                                                          0L);
                    } else {
                        pathData1 = search(graph, 
                                           middle, 
                                           sol,
                                           h, 
                                           g.get(sol) - g.get(middle),
                                           depth + 1,
                                           memoryStatistics);
                    }
                    
                    List<GridGraph.Cell> leftPath = 
                        new ArrayList<>(pathData0.path());
                    
                    List<GridGraph.Cell> rightPath = 
                        new ArrayList<>(pathData1.path());
                    
                    leftPath.addAll(rightPath.subList(1, rightPath.size()));
                    
                    if (depth == 0) {
                        long searchMillis = System.currentTimeMillis() - t;
                        
                        return getPathData(leftPath, 
                                           open, 
                                           closed, 
                                           g, 
                                           ancestors, 
                                           searchMillis,
                                           memoryStatistics);
                    } else {
                        return new GridGraphPathData(leftPath,
                                                     0L,
                                                     0L,
                                                     0L);
                    }
                }
            }
            
            if (l > 0) {
                open.set(l - 1, null); // Forget previous open frontier.
            }
            
            if (1 < l && l <= relay || l > relay + 1) {
                closed.set(l - 1, null);
            }
            
            ++l;
            open.addLast(new DoublePriorityBinaryHeap<>());
            closed.addLast(new HashSet<>());
        }
        
        long searchMillis = System.currentTimeMillis();
        
        return getPathData(List.of(),
                           open,
                           closed, 
                           g, 
                           ancestors, 
                           searchMillis,
                           memoryStatistics);
    }
    
    private static GridGraph.Cell
         expandNode(GridGraph graph,
                    GridGraph.Cell n,
                    GridGraph.Cell source,
                    GridGraph.Cell target, 
                    int l,
                    int relay,
                    int u,
                    List<DoublePriorityBinaryHeap<GridGraph.Cell>> open,
                    List<Set<GridGraph.Cell>> closed,
                    Map<GridGraph.Cell, Integer> g,
                    Map<GridGraph.Cell, GridGraph.Cell> ancestors,
                    GridGraphHeuristicFunction h) {
             
        List<GridGraph.Cell> successors = n.getNeighbours(graph);
        
        for (GridGraph.Cell neighbour : successors) {
            if (g.get(n) + 1 + h.estimate(neighbour, target) > u) {
                continue;
            }
            
            if (l > 0) {
                if (closed.get(l - 1).contains(neighbour)) {
                    continue;
                }
            }
            
            if (closed.get(l).contains(neighbour)
                    || open.get(l).containsDatum(neighbour)
                    || open.get(l + 1).containsDatum(neighbour)) {
                continue;
            }
         
            g.put(neighbour, g.get(n) + 1);
            
            if (l < relay) {
                ancestors.put(neighbour, source);
            } else if (l == relay) {
                ancestors.put(neighbour, n);
            } else {
                ancestors.put(neighbour, ancestors.get(n));
            }
            
            if (neighbour.equals(target)) {
                return neighbour;
            }
            
            open.get(l + 1)
                .insert(neighbour,
                        g.get(neighbour) + h.estimate(neighbour, target));
        }
        
        return null;
    }
         
    private static void checkU(int u) {
        if (u < 1) {
            throw new IllegalArgumentException("u(%d) < 1".formatted(u));
        }
    }
    
    private static GridGraphPathData
        getPathData(List<GridGraph.Cell> path,
                    List<DoublePriorityBinaryHeap<GridGraph.Cell>> open,
                    List<Set<GridGraph.Cell>> closed,
                    Map<GridGraph.Cell, Integer> g,
                    Map<GridGraph.Cell, GridGraph.Cell> ancestors,
                    long searchMillis,
                    boolean memoryStatistics) {
            
        if (!memoryStatistics) {
            return new GridGraphPathData(path, -1L, -1L, -1L);
        }
            
        long t = System.nanoTime();
        
        System.gc();
        
        long gcNanos = System.nanoTime() - t;
        
        GraphLayout layout = GraphLayout.parseInstance(open, 
                                                       closed, 
                                                       g, 
                                                       ancestors);
        
        long totalBytes = layout.totalSize();
        
        return new GridGraphPathData(path,
                                     searchMillis, 
                                     totalBytes,
                                     gcNanos);
    }
}
