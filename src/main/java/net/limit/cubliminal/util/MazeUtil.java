package net.limit.cubliminal.util;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Face;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class MazeUtil {

    public static Direction getDirection(Face face) {
        return switch (face) {
            case UP -> Direction.EAST;
            case DOWN -> Direction.WEST;
            case LEFT -> Direction.NORTH;
            case RIGHT -> Direction.SOUTH;
        };
    }

    public static Direction getDirection(char direction) {
        return switch (direction) {
            case 'e' -> Direction.EAST;
            case 'w' -> Direction.WEST;
            case 'n' -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    public static Face getFace(char direction) {
        return switch (direction) {
            case 'e' -> Face.UP;
            case 'w' -> Face.DOWN;
            case 'n' -> Face.LEFT;
            default -> Face.RIGHT;
        };
    }

    public static String rotString(Face face) {
        return switch (face) {
            case UP -> "e";
            case DOWN -> "w";
            case LEFT -> "n";
            case RIGHT -> "s";
        };
    }

    public static Direction rotDir(char direction, Manipulation manip) {
        Direction dir = getDirection(direction);
        BlockRotation rotation = manip.getMirror().getRotation(dir);

        Direction dir2 = rotation == BlockRotation.CLOCKWISE_180 ? dir.getOpposite() : dir;
        dir2 = switch (manip.getRotation()) {
            case NONE -> dir2;
            case CLOCKWISE_90 -> dir2.rotateYClockwise();
            case CLOCKWISE_180 -> dir2.getOpposite();
            case COUNTERCLOCKWISE_90 -> dir2.rotateYCounterclockwise();
        };
        return dir2;
    }

    // Direction utils
    public static byte ordinal(Direction direction) {
        return (byte) ("ewns".indexOf(direction.getName().charAt(0)));
    }

    public static byte faceOrdinal(Face face) {
        return switch (face) {
            case UP -> (byte) 0;
            case DOWN -> (byte) 1;
            case LEFT -> (byte) 2;
            case RIGHT -> (byte) 3;
        };
    }

    public static Direction byId(byte id) {
        return switch (id) {
            case 0 -> Direction.EAST;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    public static Face getById(byte id) {
        return switch (id) {
            case 0 -> Face.UP;
            case 1 -> Face.DOWN;
            case 2 -> Face.LEFT;
            case 3 -> Face.RIGHT;
            default -> throw new IllegalArgumentException("Can't resolve face byte reference: " + id);
        };
    }
    // -------------------------------------------------------------

    public static byte pack(Manipulation manipulation) {
        return switch (manipulation) {
            case NONE -> 0;
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case COUNTERCLOCKWISE_90 -> 3;
            case FRONT_BACK -> 4;
            case LEFT_RIGHT -> 5;
            case TOP_LEFT_BOTTOM_RIGHT -> 6;
            case TOP_RIGHT_BOTTOM_LEFT -> 7;
        };
    }

    public static Manipulation unpack(byte id) {
        return switch (id) {
            case 0 -> Manipulation.NONE;
            case 1 -> Manipulation.CLOCKWISE_90;
            case 2 -> Manipulation.CLOCKWISE_180;
            case 3 -> Manipulation.COUNTERCLOCKWISE_90;
            case 4 -> Manipulation.FRONT_BACK;
            case 5 -> Manipulation.LEFT_RIGHT;
            case 6 ->  Manipulation.TOP_LEFT_BOTTOM_RIGHT;
            default -> Manipulation.TOP_RIGHT_BOTTOM_LEFT;
        };
    }

    public static String rotString(Face face, Manipulation manip) {
        BlockRotation rotation = manip.getMirror().getRotation(getDirection(face));

        Face face2 = rotation == BlockRotation.CLOCKWISE_180 ? face.mirror() : face;
        face2 = switch (manip.getRotation()) {
            case NONE -> face2;
            case CLOCKWISE_90 -> face2.clockwise();
            case CLOCKWISE_180 -> face2.mirror();
            case COUNTERCLOCKWISE_90 -> face2.anticlockwise();
        };

        return rotString(face2);
    }

    public static Manipulation get(Face face) {
        return switch (face) {
            case UP -> Manipulation.NONE;
            case DOWN -> Manipulation.CLOCKWISE_180;
            case LEFT -> Manipulation.COUNTERCLOCKWISE_90;
            case RIGHT -> Manipulation.CLOCKWISE_90;
        };
    }

    public static Manipulation getOpposite(Manipulation manipulation) {
        BlockRotation rotation = switch (manipulation.getRotation()) {
            case NONE -> BlockRotation.NONE;
            case CLOCKWISE_90 -> BlockRotation.COUNTERCLOCKWISE_90;
            case CLOCKWISE_180 -> BlockRotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90 -> BlockRotation.CLOCKWISE_90;
        };
        return Manipulation.of(rotation, manipulation.getMirror());
    }

    public static boolean fits(MazeComponent.Vec2i vec, int mazeWidth, int mazeHeight) {
        return vec.getX() >= 0 && vec.getX() < mazeWidth && vec.getY() >= 0 && vec.getY() < mazeHeight;
    }
}
