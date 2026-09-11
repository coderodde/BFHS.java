package io.github.coderodde.graph.pathfinding.bnfs.demo;

import io.github.coderodde.graph.pathfinding.bnfs.BFHS;
import io.github.coderodde.graph.pathfinding.bnfs.BFS;
import io.github.coderodde.graph.pathfinding.bnfs.BiBFS;
import io.github.coderodde.graph.pathfinding.bnfs.GridGraph;
import io.github.coderodde.graph.pathfinding.bnfs.GridGraphPathData;
import io.github.coderodde.graph.pathfinding.bnfs.ManhattanHeuristicFunction;
import static io.github.coderodde.graph.pathfinding.bnfs.demo.Demo.pathsAreEquivalent;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class implements the demonstration of the pathfinding algorithms in grid
 * graphs.
 */
public final class DemoTest {
    
    private static final Random RANDOM = new Random(10L);
    private static final GridGraph GRAPH = new GridGraph(1000); // 1000 x 1000
    private static final int WALLS = 50_000;
    
    @Test
    public void testDemo() {
        GridGraph.Cell source = GRAPH.getCell(200, 200);
        GridGraph.Cell target = GRAPH.getCell(1_000 - 200, 1_000 - 200);
        
        GridGraphPathData data1 = BFS.search(GRAPH, 
                                             source,
                                             target);
        
        System.out.println("BFS:   " + data1);
        
        GridGraphPathData data2 = BiBFS.search(GRAPH, 
                                               source, 
                                               target);
        
        System.out.println("BiBFS: " + data2);
        
        GridGraphPathData data3 = 
            BFHS.search(GRAPH,
                        source,
                        target,
                        new ManhattanHeuristicFunction(),
                        2000);
        
        System.out.println("BFHS:  " + data3);
        
        boolean pathsEquivalent = 
                pathsAreEquivalent(GRAPH, data1.path(), data2.path()) 
             && pathsAreEquivalent(GRAPH, data1.path(), data3.path());
        
        assertTrue(pathsEquivalent);
    }
}
