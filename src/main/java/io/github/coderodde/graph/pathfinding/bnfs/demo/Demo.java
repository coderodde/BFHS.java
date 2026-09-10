package io.github.coderodde.graph.pathfinding.bnfs.demo;

import io.github.coderodde.graph.pathfinding.bnfs.BFS;
import io.github.coderodde.graph.pathfinding.bnfs.BFHS;
import io.github.coderodde.graph.pathfinding.bnfs.BiBFS;
import io.github.coderodde.graph.pathfinding.bnfs.GridGraph;
import io.github.coderodde.graph.pathfinding.bnfs.ManhattanHeuristicFunction;
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
        setRandomWalls();
        
        GridGraph.Cell source = GRAPH.getCell(10, 10);
        GridGraph.Cell target = GRAPH.getCell(1_000 - 10, 1_000 - 10);
        
        long t = System.currentTimeMillis();
        
        List<GridGraph.Cell> path1 = BFS.search(GRAPH, 
                                                source,
                                                target);
        
        System.out.printf("BFS in   %d ms.%n", System.currentTimeMillis() - t);
        
        t = System.currentTimeMillis();
        
        List<GridGraph.Cell> path2 = BiBFS.search(GRAPH, 
                                                  source, 
                                                  target);
        
        System.out.printf("BiBFS in %d ms.%n", System.currentTimeMillis() - t);
        
        t = System.currentTimeMillis();
        
        List<GridGraph.Cell> path3 = 
            BFHS.search(GRAPH,
                        source,
                        target,
                        new ManhattanHeuristicFunction(),
                        3000);
        
        System.out.printf("BFHS in  %d ms.%n", System.currentTimeMillis() - t);
        
        boolean pathsEquivalent = 
                pathsAreEquivalent(GRAPH, path1, path2) 
             && pathsAreEquivalent(GRAPH, path1, path3);
        
        System.out.printf("Algorithms agree: %b.%n", pathsEquivalent);
        
        System.out.println(path1.size());
        System.out.println(path2.size());
        System.out.println(path3.size());
    }
    
    private static boolean 
        pathsAreEquivalent(
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
