package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the graph nodes.
 */
public final class DirectedGraphNode {
    
    private final int id;
    private final Set<DirectedGraphNode> children = new HashSet<>();
    private final Set<DirectedGraphNode> parents  = new HashSet<>();
    
    public DirectedGraphNode(int id) {
        this.id = id;
    }
    
    public void connectTo(DirectedGraphNode child) {
        children.add(child);
        child.parents.add(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DirectedGraphNode other) {
            return id == other.id;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
