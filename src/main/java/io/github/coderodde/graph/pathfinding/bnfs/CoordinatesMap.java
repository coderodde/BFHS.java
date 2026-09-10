package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public final class CoordinatesMap {
    
    private final Map<DirectedGraphNode, Coordinates2D> map = new HashMap<>();
    
    public void put(DirectedGraphNode node, Coordinates2D coords) {
        map.put(node, coords);
    }
    
    public Coordinates2D get(DirectedGraphNode node) {
        return map.get(node);
    }
    
    public static final record Coordinates2D(int x, int y) {
        
    }
}
