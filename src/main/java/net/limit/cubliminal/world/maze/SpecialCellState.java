package net.limit.cubliminal.world.maze;

import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.function.Consumer;

public interface SpecialCellState {
    Identifier nbtId(NbtGroup nbtGroup, Random random);
    void decorate(Consumer<Manipulation> generateNbt);
}
