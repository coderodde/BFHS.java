package io.github.coderodde.graph.pathfinding.bnfs;

import org.junit.Test;
import static org.junit.Assert.*;

public final class BFHSTest {
    
    private final GridGraphHeuristicFunction h = new ManhattanHeuristicFunction();
    
    @Test
    public void optimalityTest1() {
        GridGraph gg = new GridGraph(2);
        
        GridGraphPathData data = 
            BFHS.search(gg, 
                        gg.getCell(0, 0), 
                        gg.getCell(1, 1), 
                        h, 
                        2);
        
        
        System.out.println(data.path());
        assertEquals(3, data.path().size());
    }
}
