package io.github.coderodde.graph.pathfinding.bfhs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a grid graph.
 */
public final class GridGraph {
    
    public enum CellType {
        PASSABLE,
        BLOCKED;
    }
    
    private final Cell[][] grid;
    
    public GridGraph(int width, int height) {
        grid = new Cell[height][];
        
        for (int y = 0; y < height; ++y) {
            Cell[] row = new Cell[width];
            
            for (int x = 0; x < width; ++x) {
                row[x] = new Cell(x, y, CellType.PASSABLE);
            }
            
            grid[y] = row;
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
    
    public Cell getCell(int x, int y) {
        return grid[y][x];
    }
    
    public void setCellType(int x, int y, CellType cellType) {
        grid[y][x].setCellType(cellType);
    }
    public boolean isPassable(int x, int y) {
        return grid[y][x].getCellType() == CellType.PASSABLE;
    }
    
    public boolean isBlocked(int x, int y) {
        return grid[y][x].getCellType() == CellType.BLOCKED;
    }
    
    public static final class Cell {
        
        private final int x;
        private final int y;
        private CellType cellType;
        
        public Cell(int x, int y, CellType cellType) {
            this.x = x;
            this.y = y;
            this.cellType = cellType;
        }
        
        public int x() {
            return x;
        }
        
        public int y() {
            return y;
        }
        
        public CellType getCellType() {
            return cellType;
        }
        
        public boolean isPassable() {
            return cellType == CellType.PASSABLE;
        }
        
        public boolean isBlocked() {
            return cellType == CellType.BLOCKED;
        }
        
        public void setCellType(CellType cellType) {
            this.cellType = cellType;
        }
    
        @Override
        public boolean equals(Object o) {
            if (o instanceof Cell cc) {
                return cc.x == x && cc.y == y;
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return 32 * Integer.hashCode(x) + Integer.hashCode(y);
        }

        public List<Cell> getNeighbours(GridGraph gridGraph) {
            List<Cell> neighbours = new ArrayList<>(4);
            Cell cell;
            
            if (x > 0) {
                cell = gridGraph.getCell(x - 1, y);
                
                if (cell.isPassable()) {
                    neighbours.addLast(cell);
                }
            }

            if (x < gridGraph.width() - 1) {
                cell = gridGraph.getCell(x + 1, y);
                
                if (cell.isPassable()) {
                    neighbours.addLast(cell);
                }
            }

            if (y > 0) {
                cell = gridGraph.getCell(x, y - 1);
                
                if (cell.isPassable()) {
                    neighbours.addLast(cell);
                }
            }

            if (y < gridGraph.height() - 1) {
                cell = gridGraph.getCell(x, y + 1);
                
                if (cell.isPassable()) {
                    neighbours.addLast(cell);
                }
            }

            return neighbours;
        }
        
        @Override
        public String toString() {
            return "[x = %d, y = %d, passable = %b]"
                    .formatted(
                            x, 
                            y,
                            cellType == CellType.PASSABLE);
        }
    }
}
