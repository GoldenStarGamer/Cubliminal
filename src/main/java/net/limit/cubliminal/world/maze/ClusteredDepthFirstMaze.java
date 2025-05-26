package net.limit.cubliminal.world.maze;

import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.maze.DepthLikeMaze;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class ClusteredDepthFirstMaze extends DepthLikeMaze {
    private final BlockPos mazePos;
    private final Random random;
    private final float bias;
    private final List<Vec2i> checkpoints;
    private final List<Vec2i> parkingSpots;

    public ClusteredDepthFirstMaze(int width, int height, BlockPos mazePos, Random random, float bias, List<Vec2i> checkpoints, List<Vec2i> parkingSpots) {
        super(width, height);
        this.mazePos = mazePos;
        this.random = random;
        this.bias = bias;
        this.checkpoints = checkpoints;
        this.parkingSpots = parkingSpots;
    }

    @Override
    public void create() {
        // Generate randomly an elevator
        Random horizontalRandom = Random.create(LimlibHelper.blockSeed(mazePos.getX(), 0, mazePos.getZ()));
        Vec2i elevator = new Vec2i(horizontalRandom.nextBetween(-8, width + 8), horizontalRandom.nextBetween(-8, height + 8));

        if (this.fits(elevator) && !parkingSpots.contains(elevator)) {
            // If the selected elevator connecting hall is inside the maze, append the nbt
            Face elevatorHall = Face.values()[horizontalRandom.nextInt(Face.values().length)];
            if (this.fits(elevator.go(elevatorHall)) && !parkingSpots.contains(elevator.go(elevatorHall))) {
                // Leave the elevator cell empty and append the directions for cell decoration
                checkpoints.remove(elevator);
                this.visit(elevator);
                this.append(elevator, "elevator", elevatorHall);
                this.append(elevator.go(elevatorHall), "elevatorHall", elevatorHall.mirror());
                checkpoints.add(elevator.go(elevatorHall));
            }
        }

        // Generate randomly a ramp
        if (!parkingSpots.isEmpty() && horizontalRandom.nextFloat() > 0.6) {
            Vec2i rampBeginning = parkingSpots.get(horizontalRandom.nextInt(parkingSpots.size()));
            Face randomDir = Face.values()[horizontalRandom.nextInt(Face.values().length)];

            if (parkingSpots.contains(rampBeginning) && parkingSpots.contains(rampBeginning.go(randomDir).go(randomDir))) {
                this.appendDeferred(rampBeginning, "ramp", (byte) 1, randomDir);
                this.appendDeferred(rampBeginning.go(randomDir), "ramp", (byte) 2, randomDir);
                this.appendDeferred(rampBeginning.go(randomDir).go(randomDir), "ramp", (byte) 3, randomDir);
            }
        }

        Vec2i cell = checkpoints.remove(random.nextInt(checkpoints.size()));
        Vec2i end = checkpoints.remove(random.nextInt(checkpoints.size()));
        this.visit(cell);
        this.visitedCells++;
        this.stack.push(cell);

        while (!this.stack.isEmpty()) {
            // Reassign current cell
            cell = this.stack.peek();
            // If it is the desired end, reassign and remove the new one
            if (cell.equals(end)) {
                if (checkpoints.isEmpty()) {
                    break;
                }

                end = checkpoints.remove(random.nextInt(checkpoints.size()));

                for (CellState cellState : this.maze) {
                    if (!cellState.getExtra().containsKey("elevator")) {
                        this.visit(cellState.getPosition(), false);
                    }
                }
            } else {
                checkpoints.remove(cell);
            }

            List<Face> neighbours = new ArrayList<>();
            List<Face> optNeighbours = new ArrayList<>();
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
                List<Face> possibilities;

                if (followingPath || random.nextInt(8) > 0) {
                    possibilities = optNeighbours;
                } else {
                    possibilities = neighbours;
                }

                Face nextFace;

                // Determine whether the next cell is going to continue straight ahead
                if (random.nextFloat() > bias && possibilities.contains(this.dir(cell))) {
                    nextFace = this.dir(cell);
                } else {
                    nextFace = possibilities.get(random.nextInt(possibilities.size()));
                }

                this.cellState(cell).go(nextFace);
                this.cellState(cell.go(nextFace)).go(nextFace.mirror());
                this.visit(cell.go(nextFace));
                this.stack.push(cell.go(nextFace));

                this.visitedCells++;
            } else {
                this.stack.pop();
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

    public void append(Vec2i vec, String key, Face dir) {
        NbtCompound appendage = new NbtCompound();
        appendage.putByte(key, (byte) dir.ordinal());
        cellState(vec).getExtra().put(key, appendage);
    }

    public void appendDeferred(Vec2i vec, String key, byte type, Face dir) {
        NbtCompound appendage = new NbtCompound();
        byte[] bytes = {type, (byte) dir.ordinal()};
        appendage.putByteArray(key, bytes);
        cellState(vec).getExtra().put(key, appendage);
    }

    public List<Vec2i> getParkingSpots() {
        return this.parkingSpots;
    }
}
