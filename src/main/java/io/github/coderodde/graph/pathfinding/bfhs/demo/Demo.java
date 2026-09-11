package io.github.coderodde.graph.pathfinding.bfhs.demo;

import io.github.coderodde.graph.pathfinding.bfhs.BFS;
import io.github.coderodde.graph.pathfinding.bfhs.BFHS;
import io.github.coderodde.graph.pathfinding.bfhs.BiBFS;
import io.github.coderodde.graph.pathfinding.bfhs.GridGraph;
import io.github.coderodde.graph.pathfinding.bfhs.GridGraphPathData;
import io.github.coderodde.graph.pathfinding.bfhs.ManhattanHeuristicFunction;
import java.util.List;
import java.util.Random;

/**
 * This class implements the demonstration of the pathfinding algorithms in grid
 * graphs.
 */
public final class Demo {
    
    private static final Random RANDOM = new Random(10L);
    private static final GridGraph GRAPH = new GridGraph(1000); // 1000 x 1000
    private static final int WALLS = 50_000;
    
    public static void main(String[] args) {
        boolean memoryStatistics = false;

        if (args.length > 0 && args[0].equals("-m")) {
            memoryStatistics = true;
        }
            
        demoGridGraph(memoryStatistics);
    }
    
    public static void demoGridGraph(boolean memoryStatistics) {
        setRandomWalls();
        
        GridGraph.Cell source = GRAPH.getCell(200, 200);
        GridGraph.Cell target = GRAPH.getCell(1_000 - 200, 1_000 - 200);
        
        GridGraphPathData data1 = BFS.search(GRAPH, 
                                             source,
                                             target,
                                             memoryStatistics);
        
        System.out.println("BFS:   " + data1);
        
        GridGraphPathData data2 = BiBFS.search(GRAPH, 
                                               source, 
                                               target,
                                               memoryStatistics);
        
        System.out.println("BiBFS: " + data2);
        
        GridGraphPathData data3 = 
            BFHS.search(GRAPH,
                        source,
                        target,
                        new ManhattanHeuristicFunction(),
                        2000,
                        memoryStatistics);
        
        System.out.println("BFHS:  " + data3);
        
        boolean pathsEquivalent = 
                pathsAreEquivalent(GRAPH, data1.path(), data2.path()) 
             && pathsAreEquivalent(GRAPH, data1.path(), data3.path());
        
        System.out.printf("Algorithms agree: %b.%n", pathsEquivalent);
    }
    
    static boolean pathsAreEquivalent(
            GridGraph graph, 
            List<GridGraph.Cell> path1, 
            List<GridGraph.Cell> path2) {
        
        if (path1.size() != path2.size()) {
            return false;
        }
        
        // Check sources:
        if (!path1.getFirst().equals(path2.getFirst())) {
            return false;
        }
        
        // Check targets:
        if (!path1.getLast().equals(path2.getLast())) {
            return false;
        }
        
        if (pathIsBroken(graph, path1)) {
            return false;
        }
        
        return !pathIsBroken(graph, path2);
    }
        
    private static boolean pathIsBroken(GridGraph graph, 
                                        List<GridGraph.Cell> path) {
        
        for (int i = 0; i < path.size() - 1; ++i) {
            GridGraph.Cell a = path.get(i);
            GridGraph.Cell b = path.get(i + 1);
            
            List<GridGraph.Cell> neighboursOfA = a.getNeighbours(graph);
            
            if (!neighboursOfA.contains(b)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static void setRandomWalls() {
        for (int i = 0; i < WALLS; ++i) {
            int x = RANDOM.nextInt(GRAPH.width());
            int y = RANDOM.nextInt(GRAPH.height());
            
            GRAPH.setCellType(x, y, GridGraph.CellType.BLOCKED);
        }
    }
}
