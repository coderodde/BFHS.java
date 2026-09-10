package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public final class BFHSTest {
    
    private final HeuristicFunction h = new ManhattanHeuristicFunction();
    
    @Test
    public void optimalityTest1() {
        GridGraph gg = new GridGraph(2);
        
        List<GridGraph.Cell> path = 
            BFHS.search(gg, 
                        gg.getCell(0, 0), 
                        gg.getCell(1, 1), 
                        h, 
                        2);
        
        
        System.out.println(path);
        assertEquals(3, path.size());
    }
}
