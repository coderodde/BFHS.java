package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the BFHS (Breadth-first heuristic search) algorithm
 * discussed in the paper
 * <a href="https://www.sciencedirect.com/science/article/pii/S0004370205002158">Breadth-first heuristic search</a>.
 */
public final class BHFS {

    private BHFS() {}
    
    public List<GridGraph.Cell> search(GridGraph graph,
                                       GridGraph.Cell source,
                                       GridGraph.Cell target,
                                       HeuristicFunction h,
                                       boolean diagonal,
                                       int u) {
        
        checkU(u);
        
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
                    expandNode(graph, 
                        n,         // Node to expand.
                        source,    // The source node
                        target,    // The target node.e
                        l,         // The level/depth counter.
                        relay,     // The index of the relay layer.
                        u,         // The upper bound on the path.
                        diagonal,  // If true, diagonal movements allowed.
                        open,      // The open set stack.
                        closed,    // The closed set stack.
                        g,         // Maps each node to cost.
                        ancestors, // Maps each node to ancestor.
                        h);        // The heuristic function.
                
                List<GridGraph.Cell> path0;
                List<GridGraph.Cell> path1;
                
                if (sol != null) {
                    GridGraph.Cell middle = ancestors.get(sol);
                    
                    if (g.get(middle) == 1) {
                        path0 = List.of(source, middle);
                    } else {
                        path0 = search(graph, 
                                       source,
                                       middle, 
                                       h, 
                                       diagonal, 
                                       g.get(middle));
                    }
                    
                    if (g.get(sol) - g.get(middle) == 1) {
                        path1 = List.of(middle, sol);
                    } else {
                        path1 = search(graph, 
                                       middle, 
                                       sol,
                                       h, 
                                       diagonal, 
                                       g.get(sol) - g.get(middle));
                    }
                    
                    path0.addAll(path1);
                    return path0;
                }
            }
            
            if (1 < l && l <= relay || l > relay + 1) {
                closed.get(l - 1).clear();
            }
            
            ++l;
            open.get(l + 1).clear();
            closed.get(l).clear();
        }
        
        return List.of();
    }
    
    public List<GridGraph.Cell> search(GridGraph graph,
                                        GridGraph.Cell source,
                                        GridGraph.Cell target,
                                        HeuristicFunction h,
                                        boolean diagonal) {
        
        int u = (int)(h.estimate(source, target) / 2.0);
        
        return search(graph, 
                      source, 
                      target, 
                      h, 
                      diagonal,
                      u);
    }
    
    private static GridGraph.Cell
         expandNode(GridGraph graph,
                    GridGraph.Cell n,
                    GridGraph.Cell source,
                    GridGraph.Cell target, 
                    int l,
                    int relay,
                    int u,
                    boolean diagonal,
                    List<DoublePriorityBinaryHeap<GridGraph.Cell>> open,
                    List<Set<GridGraph.Cell>> closed,
                    Map<GridGraph.Cell, Integer> g,
                    Map<GridGraph.Cell, GridGraph.Cell> ancestors,
                    HeuristicFunction h) {
             
        List<GridGraph.Cell> successors = 
            diagonal ? n.getBasicNeighbours (graph) 
                     : n.getAllNeighbours   (graph);
        
        for (GridGraph.Cell neighbour : successors) {
            if (g.get(n) + 1 + h.estimate(n, target) > u) {
                continue;
            }
            
            if (closed.get(l - 1).contains(neighbour) 
                    || closed.get(l).contains(neighbour)
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
}
