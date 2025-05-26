package net.limit.cubliminal.world.room;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import net.limit.cubliminal.util.MazeUtil;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

// TODO: SHOULD BE REMOVED
public class RoomVariant {

    private String nbt;
    private final byte width, height;
    //private final Byte2ObjectArrayMap<ArrayList<SingleRoom.Door>> doors;
    private final byte manipulation;

    public RoomVariant(SingleRoom parent, Manipulation manipulation) {
        //this.nbt = parent.id();
        this.width = switch (manipulation.getRotation()) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> {
                this.height = parent.width();
                yield parent.height();
            }
            default -> {
                this.height = parent.height();
                yield parent.width();
            }
        };
        this.manipulation = MazeUtil.pack(manipulation);
        //this.doors = this.transformDoors(parent.doors(), packedManipulation);
    }

    private Byte2ObjectArrayMap<ArrayList<SingleRoom.Door>> transformDoors(SingleRoom.Door[] parentDoors, Manipulation manipulation) {
        Byte2ObjectArrayMap<ArrayList<SingleRoom.Door>> doors = new Byte2ObjectArrayMap<>();
        for (SingleRoom.Door door : parentDoors) {
            doors.computeIfAbsent(door.facing(), key -> new ArrayList<>()).add(this.transformDoor(door, manipulation));
        }
        return doors;
    }

    public final SingleRoom.Door transformDoor(SingleRoom.Door door, Manipulation manipulation) {
        byte newX;
        byte newY;
        Direction newFacing = switch (manipulation.getRotation()) {
            case CLOCKWISE_90 -> {
                newX = (byte) (this.height - 1 - door.relativeY());
                newY = door.relativeX();
                yield MazeUtil.byId(door.facing()).rotateYClockwise();
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = door.relativeY();
                newY = (byte) (this.width - 1 - door.relativeX());
                yield MazeUtil.byId(door.facing()).rotateYCounterclockwise();
            }
            case CLOCKWISE_180 -> {
                newX = (byte) (this.height - 1 - door.relativeX());
                newY = (byte) (this.width - 1 - door.relativeY());
                yield MazeUtil.byId(door.facing()).getOpposite();
            }
            case NONE -> {
                newX = door.relativeX();
                newY = door.relativeY();
                yield MazeUtil.byId(door.facing());
            }
        };

        if (manipulation.getMirror() == BlockMirror.LEFT_RIGHT) {
            newY = (byte) (this.width - 1 - newY);
        } else if (manipulation.getMirror() == BlockMirror.FRONT_BACK) {
            newX = (byte) (this.height - 1 - newX);
        }
        BlockRotation mirrorRotation = manipulation.getMirror().getRotation(newFacing);
        if (mirrorRotation == BlockRotation.CLOCKWISE_180) {
            newFacing = newFacing.getOpposite();
        }

        return new SingleRoom.Door(newX, newY, MazeUtil.ordinal(newFacing));
    }


    public String nbt() {
        return this.nbt;
    }

    public byte width() {
        return this.width;
    }

    public byte height() {
        return this.height;
    }

    public byte manipulation() {
        return this.manipulation;
    }
}