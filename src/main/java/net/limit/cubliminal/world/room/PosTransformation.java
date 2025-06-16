package net.limit.cubliminal.world.room;

import net.limit.cubliminal.world.maze.Vec2b;

@FunctionalInterface
public interface PosTransformation {
    Vec2b translate(Vec2b position);
}