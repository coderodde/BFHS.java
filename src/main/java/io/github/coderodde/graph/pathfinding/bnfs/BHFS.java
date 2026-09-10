package io.github.coderodde.graph.pathfinding.bnfs;

import io.github.coderodde.graph.pathfinding.bnfs.GridGraph.CellCoordinates;
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
    
    public List<CellCoordinates> search(GridGraph graph,
                                        CellCoordinates source,
                                        CellCoordinates target,
                                        HeuristicFunction h,
                                        boolean diagonal,
                                        int u) {
        
        Map<CellCoordinates, Integer> g                 = new HashMap<>();
        Map<CellCoordinates, CellCoordinates> ancestors = new HashMap<>();
        
        List<DoublePriorityBinaryHeap<CellCoordinates>> open = 
            new ArrayList<>();
        
        List<Set<CellCoordinates>> closed = new ArrayList<>();
        
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
                CellCoordinates n = open.get(l).extractTop();
                closed.get(l).add(n);
                CellCoordinates sol = expandNode(graph, 
                                                 n,
                                                 source,
                                                 target, 
                                                 l,
                                                 relay, 
                                                 u, 
                                                 diagonal, 
                                                 open, 
                                                 closed, 
                                                 g, 
                                                 ancestors, 
                                                 h);
                
                List<CellCoordinates> path0;
                List<CellCoordinates> path1;
                
                if (sol != null) {
                    CellCoordinates middle = ancestors.get(sol);
                    
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
                
            }
            
            ++l;
            open.get(l + 1).clear();
            closed.get(l).clear();
        }
        
        return List.of();
    }
    
    private static CellCoordinates
         expandNode(GridGraph graph,
                    CellCoordinates n,
                    CellCoordinates source,
                    CellCoordinates target, 
                    int l,
                    int relay,
                    int u,
                    boolean diagonal,
                    List<DoublePriorityBinaryHeap<CellCoordinates>> open,
                    List<Set<CellCoordinates>> closed,
                    Map<CellCoordinates, Integer> g,
                    Map<CellCoordinates, CellCoordinates> ancestors,
                    HeuristicFunction h) {
             
        List<CellCoordinates> successors = 
            diagonal ? 
                graph.getAllNeighbours  (n.x(), n.y()) : 
                graph.getBasicNeighbours(n.x(), n.y());       
        
        for (CellCoordinates neighbour : successors) {
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
}
