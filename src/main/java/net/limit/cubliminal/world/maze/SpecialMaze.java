package net.limit.cubliminal.world.maze;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;

public class SpecialMaze extends MazeComponent {
    public SpecialMaze(int width, int height) {
        super(width, height);
    }

    @Override
    public void create() {
    }

    public void withState(int x, int y, CellState cellState) {
        if (cellState instanceof SpecialCellState) {
            this.maze[y * this.width + x] = cellState;
        }
    }
}
