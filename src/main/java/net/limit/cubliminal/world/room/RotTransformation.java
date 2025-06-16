package net.limit.cubliminal.world.room;

import net.minecraft.util.math.Direction;

@FunctionalInterface
public interface RotTransformation {
    Direction rotate(Direction direction);
}