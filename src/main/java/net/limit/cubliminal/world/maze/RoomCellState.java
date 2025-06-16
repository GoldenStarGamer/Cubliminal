package net.limit.cubliminal.world.maze;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import net.limit.cubliminal.world.room.Room;
import net.limit.cubliminal.world.room.RoomPlacement;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.function.Consumer;

public class RoomCellState extends CellState implements SpecialCellState {

    private RoomPlacement roomPlacement;
    private Byte2ObjectArrayMap<ArrayList<Room.Door>> doors;

    public RoomCellState(RoomPlacement roomPlacement) {
        this.roomPlacement = roomPlacement;
    }

    public RoomPlacement getRoom() {
        return this.roomPlacement;
    }

    public void room(RoomPlacement roomPlacement) {
        this.roomPlacement = roomPlacement;
    }

    public Byte2ObjectArrayMap<ArrayList<Room.Door>> getDoors() {
        return this.doors;
    }

    public void doors(Byte2ObjectArrayMap<ArrayList<Room.Door>> doors) {
        this.doors = doors;
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
