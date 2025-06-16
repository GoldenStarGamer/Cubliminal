package net.limit.cubliminal.client.sound;

import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NoclipSoundInstance extends MovingSoundInstance {
    public static NoclipSoundInstance WALL_CLIPPING = new NoclipSoundInstance(
            Objects.requireNonNull(MinecraftClient.getInstance().player));
    private final PlayerEntity player;

    private <T extends PlayerEntity> NoclipSoundInstance(@NotNull T player) {
        super(CubliminalSounds.WALL_CLIPPING.value(), SoundCategory.PLAYERS, SoundInstance.createRandom());
        this.attenuationType = AttenuationType.NONE;
        this.repeat = false;
        this.player = player;
    }

    @Override
    public void tick() {
        if (!this.player.isRemoved() && this.player.isAlive()) {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
        } else {
            this.setDone();
        }
    }
}
