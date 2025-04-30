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

    public static String rotString(Face face) {
        return switch (face) {
            case UP -> "e";
            case DOWN -> "w";
            case LEFT -> "n";
            case RIGHT -> "s";
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
