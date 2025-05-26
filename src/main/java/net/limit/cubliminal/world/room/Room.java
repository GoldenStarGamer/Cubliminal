package net.limit.cubliminal.world.room;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import net.limit.cubliminal.util.MazeUtil;
import net.limit.cubliminal.world.maze.SpecialMaze;
import net.limit.cubliminal.world.maze.Vec2b;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The basic interface common to room-like objects.
 */
public interface Room {

    Codec<Room> CODEC = RoomType.REGISTRY.getCodec().dispatch("type", Room::type, RoomType::codec);

    byte width();

    byte height();

    RoomType<?> type();

    /**
     * Format: {@code e_bl_bl_bl:w_bl_bl_bl:s_bl_bl:n_bl_bl}
     * <p>Rightmost wall data can be skipped if considered irrelevant but any offset from relative 0, 0 should be specified.
     * It is read from left to right, and leftmost data represent doors closest to the west-north relative coordinate.
     * Redundant data will be ignored e.g. non-existent walls.
     **/
    default Byte2ObjectArrayMap<ArrayList<Door>> unpackDoors(String doorData) {
        String[] rawData = doorData.split(":");
        Byte2ObjectArrayMap<ArrayList<Door>> doors = new Byte2ObjectArrayMap<>();
        for (String wall : rawData) {
            char direction = wall.charAt(0);
            if ("ewns".indexOf(direction) != -1) {
                String[] wallData = wall.split("_");
                byte dir = MazeUtil.ordinal(MazeUtil.getDirection(direction));
                for (int n = 1; n < wallData.length; n++) {
                    if (wallData[n].equals("1") && this.isDoorValid(dir, n)) {
                        Door door = switch (dir) {
                            case 0 -> new Door((byte) (this.height() - 1), (byte) (n - 1), dir);
                            case 1 -> new Door((byte) 0, (byte) (n - 1), dir);
                            case 2 -> new Door((byte) (n - 1), (byte) 0, dir);
                            default -> new Door((byte) (n - 1), (byte) (this.width() - 1), dir);
                        };
                        doors.computeIfAbsent(dir, key -> new ArrayList<>()).add(door);
                    }
                }
            }
        }
        return doors;
    }

    default boolean isDoorValid(byte dir, int index) {
        return switch (dir) {
            case 0, 1 -> index <= this.width();
            default -> index <= this.height();
        };
    }

    void place(SpecialMaze maze, int x, int y, Vec2b roomDimensions, byte packedManipulation);

    default Function<Vec2b, Vec2b> transformPos(Vec2b roomDimensions, Manipulation manipulation) {
        Function<Vec2b, Vec2b> rotation = switch (manipulation.getRotation()) {
            case CLOCKWISE_90 -> pos -> new Vec2b((byte) (roomDimensions.x() - 1 - pos.y()), pos.x());
            case COUNTERCLOCKWISE_90 -> pos -> new Vec2b(pos.y(), (byte) (roomDimensions.y() - 1 - pos.x()));
            case CLOCKWISE_180 -> pos -> new Vec2b((byte) (roomDimensions.x() - 1 - pos.x()), (byte) (roomDimensions.y() - 1 - pos.y()));
            case NONE -> pos -> pos;
        };

        return rotation.andThen(switch (manipulation.getMirror()) {
            case LEFT_RIGHT -> pos -> new Vec2b(pos.x(), (byte) (roomDimensions.y() - 1 - pos.y()));
            case FRONT_BACK -> pos -> new Vec2b((byte) (roomDimensions.x() - 1 - pos.x()), pos.y());
            case NONE -> pos -> pos;
        });
    }

    default BiFunction<Vec2b, Vec2b, Vec2b> originCorner(Vec2b roomDimensions, Manipulation manipulation) {
        boolean swap = false;
        BiFunction<Vec2b, Vec2b, Vec2b> rotation = switch (manipulation.getRotation()) {
            case CLOCKWISE_90 -> {
                swap = true;
                yield (pos, hw) -> new Vec2b((byte) (roomDimensions.x() - pos.y() - hw.y()), pos.x());
            }
            case COUNTERCLOCKWISE_90 -> {
                swap = true;
                yield (pos, hw) -> new Vec2b(pos.y(), (byte) (roomDimensions.y() - pos.x() - hw.x()));
            }
            case CLOCKWISE_180 -> (pos, hw) -> new Vec2b((byte) (roomDimensions.x() - pos.x() - hw.x()), (byte) (roomDimensions.y() - pos.y() - hw.y()));
            case NONE -> (pos, hw) -> pos;
        };

        BiFunction<Vec2b, Vec2b, Vec2b> mirror = switch (manipulation.getMirror()) {
            case LEFT_RIGHT -> (pos, hw) -> new Vec2b(pos.x(), (byte) (roomDimensions.y() - pos.y() - hw.y()));
            case FRONT_BACK -> (pos, hw) -> new Vec2b((byte) (roomDimensions.x() - pos.x() - hw.x()), pos.y());
            case NONE -> (pos, hw) -> pos;
        };

        boolean finalSwap = swap;
        return (pos, hw) -> {
            Vec2b rotatedPos = rotation.apply(pos, hw);
            Vec2b updated = finalSwap ? hw.invert() : hw;
            return mirror.apply(rotatedPos, updated);
        };
    }

    default Instance newInstance(Random random) {
        return new Instance(this, this.width(), this.height(), (byte) random.nextInt(8));
    }

    record Door(byte relativeX, byte relativeY, byte facing) {
    }

    record Instance(Room parent, byte width, byte height, byte packedManipulation) {
        public Instance(Room parent, byte width, byte height, byte packedManipulation) {
            this.parent = parent;
            this.width = switch (MazeUtil.unpack(packedManipulation).getRotation()) {
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> {
                    this.height = width;
                    yield height;
                }
                default -> {
                    this.height = height;
                    yield width;
                }
            };
            this.packedManipulation = packedManipulation;
        }

        public void place(SpecialMaze maze, int x, int y) {
            this.parent.place(maze, x, y, new Vec2b(height(), width()), packedManipulation());
        }
    }
}