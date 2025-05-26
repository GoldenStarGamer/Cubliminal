package net.limit.cubliminal.world.maze;

import net.limit.cubliminal.world.room.RoomPlacement;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.function.Consumer;

public class RoomCellState extends CellState implements SpecialCellState {

    private RoomPlacement roomPlacement;

    public RoomCellState(RoomPlacement roomPlacement) {
        this.roomPlacement = roomPlacement;
    }

    public void room(RoomPlacement roomPlacement) {
        this.roomPlacement = roomPlacement;
    }

    @Override
    public CellState copy() {
        CellState cellState = super.copy();
        if (cellState instanceof RoomCellState roomed) {
            roomed.room(roomPlacement);
        }
        return cellState;
    }

    @Override
    public Identifier nbtId(NbtGroup nbtGroup, Random random) {
        String[] dir = roomPlacement.get(random).split(":", 2);
        return nbtGroup.nbtId(dir[0], dir[1]);
    }

    @Override
    public void decorate(Consumer<Manipulation> generateNbt) {
        if (roomPlacement != null) {
            generateNbt.accept(roomPlacement.manipulation());
        }
    }
}
