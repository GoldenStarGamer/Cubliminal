package net.limit.cubliminal.world.maze;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import io.github.jdiemke.triangulation.Edge2D;
import io.github.jdiemke.triangulation.Vector2D;
import net.limit.cubliminal.world.room.Room;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class LevelOneMaze extends SpecialMaze {

    private final boolean[][] filter;
    private final float bias;
    private final Random random;
    private List<Edge2D> mst;
    private SetMultimap<Vec2i, Room.DoorInstance> doors;

    public LevelOneMaze(int width, int height, boolean[][] filter, float bias, Random random) {
        super(width, height);
        this.filter = filter;
        this.bias = bias;
        this.random = random;
    }

    @Override
    public void create() {
        if (this.mst != null) {
            for (Edge2D edge : this.mst) {
                Vec2i cell = new Vec2i((int) edge.a.x, (int) edge.a.y);
                Vec2i end = new Vec2i((int) edge.b.x, (int) edge.b.y);
                this.visitedCells++;
                this.visit(cell);
                this.connectDoors(cell);
                Stack<Vec2i> corridor = new Stack<>();
                this.stack.push(cell);
                corridor.push(cell);
                while (!corridor.isEmpty()) {
                    // Reassign current cell
                    cell = corridor.peek();
                    // If it is the desired end, reassign and remove the new one
                    if (cell.equals(end)) {
                        this.connectDoors(end);
                        for (CellState cellState : this.maze) {
                            if (!cellState.getExtra().containsKey("elevator")) {
                                this.visit(cellState.getPosition(), false);
                            }
                        }
                        break;
                    }

                    List<Face> neighbours = new ArrayList<>(4);
                    List<Face> optNeighbours = new ArrayList<>(4);
                    int smallestDistance = Integer.MAX_VALUE;
                    boolean followingPath = false;

                    for (Face face : Face.values()) {
                        if (this.hasNeighbour(cell, face)) {
                            // Create two lists: one including the available neighbours and another the closest to the end
                            int distance = this.manhattanDistance(cell.go(face), end);
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
                        if (followingPath || random.nextInt(8) > 0) neighbours = optNeighbours;

                        Face nextFace;
                        // Determine whether the next cell is going to continue straight ahead
                        if (random.nextFloat() > bias && neighbours.contains(this.dir(cell))) {
                            nextFace = this.dir(cell);
                        } else {
                            nextFace = neighbours.get(random.nextInt(neighbours.size()));
                        }

                        Vec2i nextCell = cell.go(nextFace);
                        this.cellState(cell).go(nextFace);
                        this.cellState(nextCell).go(nextFace.mirror());
                        this.visit(nextCell);
                        this.stack.push(nextCell);
                        corridor.push(nextCell);
                        this.visitedCells++;
                    } else {
                        this.stack.pop();
                        corridor.pop();
                    }
                }
            }
        }
    }

    @Override
    public boolean hasNeighbour(Vec2i vec, Face face) {
        Vec2i adj = vec.go(face);
        return super.hasNeighbour(vec, face) && !this.filter[adj.getX()][adj.getY()];
    }

    public void setMst(Collection<Edge2D> mst) {
        this.mst = new ArrayList<>(mst);
    }

    public void setDoors(SetMultimap<Vec2i, Room.DoorInstance> doors) {
        this.doors = HashMultimap.create(doors);
    }

    public void connectDoors(Vec2i doorPos) {
        this.doors.get(doorPos).forEach(door -> this.cellState(doorPos).go(door.facing().mirror()));
    }
}
