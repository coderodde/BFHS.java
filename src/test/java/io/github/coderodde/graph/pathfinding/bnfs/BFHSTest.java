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
        
        assertEquals(3, data.path().size());
    }
    
    @Test
    public void optimalityTest2() {
        GridGraph gg = new GridGraph(5);
        
        GridGraphPathData data = 
            BFHS.search(gg, 
                        gg.getCell(0, 0), 
                        gg.getCell(4, 4), 
                        h, 
                        8);
        
        assertEquals(9, data.path().size());
    }
    
    @Test
    public void sourceTargetSameCells() {
        GridGraph gg = new GridGraph(4);
        GridGraph.Cell terminal = gg.getCell(2, 2);
        
        GridGraphPathData data = BFHS.search(gg, terminal, terminal, h, 0);
        
        assertEquals(1, data.path().size());
    }
}
