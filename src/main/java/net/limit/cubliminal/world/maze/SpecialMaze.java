package net.limit.cubliminal.world.maze;

import net.ludocrypt.limlib.api.world.maze.DepthLikeMaze;

public abstract class SpecialMaze extends DepthLikeMaze {

    public SpecialMaze(int width, int height) {
        super(width, height);
    }

    public void withState(int x, int y, CellState cellState) {
        if (cellState instanceof SpecialCellState) {
            this.maze[y * this.width + x] = cellState;
        }
    }

    public int manhattanDistance(Vec2i start, Vec2i end) {
        return Math.abs(end.getX() - start.getX()) + Math.abs(end.getY() - start.getY());
    }

    public Face dir(Vec2i vec) {
        return cellState(vec).getExtra().containsKey("dir") ? Face.values()[cellState(vec).getExtra().get("dir").getByte("dir")] : null;
    }
}