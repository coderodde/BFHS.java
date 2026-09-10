package io.github.coderodde.graph.pathfinding.bnfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a grid graph.
 */
public final class GridGraph {
    
    private static final boolean PASSABLE = true;
    private static final boolean BLOCKED  = false;
    
    private final boolean[][] grid;
    
    public GridGraph(int width, int height) {
        grid = new boolean[height][];
        
        for (int i = 0; i < height; ++i) {
            boolean[] row = new boolean[width];
            Arrays.fill(row, PASSABLE);
            grid[i] = row;
        }
    }
    
    public GridGraph(int widthHeight) {
        this(widthHeight, widthHeight);
    }
    
    public int width() {
        return grid[0].length;
    }
    
    public int height() {
        return grid.length;
    }
    
    public boolean isPassable(int x, int y) {
        return grid[y][x] == PASSABLE;
    }
    
    public boolean isBlocked(int x, int y) {
        return grid[y][x] == BLOCKED;
    }
    
    public void setCell(int x, int y, boolean passable) {
        grid[y][x] = passable;
    }
    
    public List<CellCoordinates> getBasicNeighbours(int x, int y) {
        List<CellCoordinates> neighbours = new ArrayList<>(4);
        
        if (x > 0) {
            neighbours.addLast(new CellCoordinates(x - 1, y));
        }
        
        if (x < width() - 1) {
            neighbours.addLast(new CellCoordinates(x + 1, y));
        }
        
        if (y > 0) {
            neighbours.addLast(new CellCoordinates(x, y - 1));
        }
        
        if (y < height() - 1) {
            neighbours.addLast(new CellCoordinates(x, y + 1));
        }
        
        return neighbours;
    }
    
    public List<CellCoordinates> getDiagonalNeighbours(int x, int y) {
        List<CellCoordinates> neighbours = new ArrayList<>(4);
        
        if (x > 0 && y > 0) {
            neighbours.addLast(new CellCoordinates(x - 1, y - 1));
        }
        
        if (x > 0 && y < height() - 1) {
            neighbours.addLast(new CellCoordinates(x - 1, y + 1));
        }
        
        if (x < width() - 1 && y > 0) {
            neighbours.addLast(new CellCoordinates(x + 1, y - 1));
        }
        
        if (x < width() - 1 && y < height() - 1) {
            neighbours.addLast(new CellCoordinates(x + 1, y + 1));
        }
        
        return neighbours;
    }
    
    public List<CellCoordinates> getAllNeighbours(int x, int y) {
        List<CellCoordinates> basicNeighbours    = getBasicNeighbours(x, y);
        List<CellCoordinates> diagonalNeighbours = getDiagonalNeighbours(x, y);
        basicNeighbours.addAll(diagonalNeighbours);
        return basicNeighbours;
    }
    
    public static final record CellCoordinates(int x, int y) {
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof CellCoordinates cc) {
                return cc.x == x && cc.y == y;
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return 32 * Integer.hashCode(x) + Integer.hashCode(y);
        }
    }
}
