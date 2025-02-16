package net.limit.cubliminal.world.maze;

import net.ludocrypt.limlib.api.world.maze.DepthLikeMaze;
import net.minecraft.util.math.random.Random;
import org.apache.commons.compress.utils.Lists;

import java.util.List;


public class ClusteredDepthFirstMaze extends DepthLikeMaze {
    private final Random random;
    private final float bias;
    private List<Vec2i> checkpoints;

    public ClusteredDepthFirstMaze(int width, int height, Random random, float bias, List<Vec2i> checkpoints) {
        super(width, height);
        this.random = random;
        this.bias = bias;
        this.checkpoints = checkpoints;
    }

    @Override
    public void create() {
        Vec2i cell = checkpoints.remove(random.nextInt(checkpoints.size()));
        Vec2i end = checkpoints.remove(random.nextInt(checkpoints.size()));
        visit(cell);
        visitedCells++;
        this.stack.push(cell);

        while (true) {
            //Cubliminal.LOGGER.info("Cell: {} ; End: {}", cell, end);
            List<Face> neighbours = Lists.newArrayList();
            List<Face> optNeighbours = Lists.newArrayList();
            int smallestDistance = Integer.MAX_VALUE;
            boolean followingPath = false;

            for (Face face : Face.values()) {
                if (this.hasNeighbour(cell, face)) {

                    // Create two lists: one including the available neighbours and another the closest to the end
                    int distance = this.distanceBetween(cell.go(face), end);
                    if (distance <= smallestDistance) {
                        // If a single cell is in the stack, remove those that aren't
                        boolean visited = this.stack.contains(cell.go(face));

                        if (distance < smallestDistance || (!followingPath && visited)) {
                            smallestDistance = distance;
                            optNeighbours.clear();
                            optNeighbours.add(face);
                            followingPath = visited;
                        } else if (visited == followingPath) {
                            optNeighbours.add(face);
                        }
                    }

                    neighbours.add(face);
                }
            }

            if (!neighbours.isEmpty()) {
                // If a short path has already been generated, follow it
                List<Face> possibilities = optNeighbours;

                /*
                if (followingPath || random.nextInt(8) > 0) {
                    possibilities = optNeighbours;
                } else possibilities = neighbours;

                 */

                Face nextFace = possibilities.get(random.nextInt(possibilities.size()));

                // Determine whether the next cell is going to continue straight ahead
                if (random.nextFloat() > bias && possibilities.contains(this.dir(cell))) {
                    nextFace = this.dir(cell);
                }

                this.cellState(cell).go(nextFace);
                this.cellState(cell.go(nextFace)).go(nextFace.mirror());
                this.visit(cell.go(nextFace));
                this.stack.push(cell.go(nextFace));

                visitedCells++;
            } else {
                //Cubliminal.LOGGER.info("Popping current cell: {}", stack.pop());
                this.stack.pop();
            }

            // Reassign current cell
            cell = this.stack.peek();

            // If it is the desired end, reassign and remove the new one
            if (cell.equals(end)) {
                if (checkpoints.isEmpty()) {
                    break;
                }

                end = checkpoints.remove(random.nextInt(checkpoints.size()));
                //Cubliminal.LOGGER.info("New end: {}", end);
                for (CellState cellState : this.maze) {
                    this.visit(cellState.getPosition(), false);
                }
            } else {
                checkpoints.remove(cell);
            }
        }
    }


    // Not using vector length to avoid zigzagging
    public int distanceBetween(Vec2i start, Vec2i end) {
        return Math.abs(end.getX() - start.getX()) + Math.abs(end.getY() - start.getY());
    }

    public Face dir(Vec2i vec) {
        return cellState(vec).getExtra().containsKey("dir") ? Face.values()[cellState(vec).getExtra().get("dir").getByte("dir")] : null;
    }
}
