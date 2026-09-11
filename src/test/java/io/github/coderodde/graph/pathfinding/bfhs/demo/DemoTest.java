package io.github.coderodde.graph.pathfinding.bfhs.demo;

import io.github.coderodde.graph.pathfinding.bfhs.BFHS;
import io.github.coderodde.graph.pathfinding.bfhs.BFS;
import io.github.coderodde.graph.pathfinding.bfhs.BiBFS;
import io.github.coderodde.graph.pathfinding.bfhs.GridGraph;
import io.github.coderodde.graph.pathfinding.bfhs.GridGraphPathData;
import io.github.coderodde.graph.pathfinding.bfhs.ManhattanHeuristicFunction;
import static io.github.coderodde.graph.pathfinding.bfhs.demo.Demo.pathsAreEquivalent;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class implements the demonstration of the pathfinding algorithms in grid
 * graphs.
 */
public final class DemoTest {
    
    private static final GridGraph GRAPH = new GridGraph(1000); // 1000 x 1000
    
    @Test
    public void testDemo() {
        GridGraph.Cell source = GRAPH.getCell(200, 200);
        GridGraph.Cell target = GRAPH.getCell(1_000 - 200, 1_000 - 200);
        
        GridGraphPathData data1 = BFS.search(GRAPH, 
                                             source,
                                             target);
        
        GridGraphPathData data2 = BiBFS.search(GRAPH, 
                                               source, 
                                               target);
        GridGraphPathData data3 = 
            BFHS.search(GRAPH,
                        source,
                        target,
                        new ManhattanHeuristicFunction(),
                        2000);
        
        boolean pathsEquivalent = 
                pathsAreEquivalent(GRAPH, data1.path(), data2.path()) 
             && pathsAreEquivalent(GRAPH, data1.path(), data3.path());
        
        assertTrue(pathsEquivalent);
    }
}
