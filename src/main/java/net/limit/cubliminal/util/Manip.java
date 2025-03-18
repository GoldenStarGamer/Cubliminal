package net.limit.cubliminal.util;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;

public class Manip {

    public static Manipulation get(MazeComponent.Face face) {
        return switch (face) {
            case UP -> Manipulation.NONE;
            case DOWN -> Manipulation.CLOCKWISE_180;
            case LEFT -> Manipulation.COUNTERCLOCKWISE_90;
            case RIGHT -> Manipulation.CLOCKWISE_90;
        };
    }
}
