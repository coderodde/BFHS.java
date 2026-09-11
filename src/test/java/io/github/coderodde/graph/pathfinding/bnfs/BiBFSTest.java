package io.github.coderodde.graph.pathfinding.bnfs;

import org.junit.Test;
import static org.junit.Assert.*;

public class BiBFSTest {
    
    @Test
    public void sourceTargetSameCells() {
        GridGraph gg = new GridGraph(4);
        GridGraph.Cell terminal = gg.getCell(2, 2);
        
        GridGraphPathData data = BiBFS.search(gg, terminal, terminal);
        
        assertEquals(1, data.path().size());
    }
    
    @Test
    public void largeTest() {
        GridGraph gg = new GridGraph(500);
        GridGraph.Cell source = gg.getCell(2, 2);
        GridGraph.Cell target = gg.getCell(500 - 2, 500 - 2);
        
        GridGraphPathData data = BiBFS.search(gg, source, target);
        
        System.out.println(data.path().size());
        
    }
}
