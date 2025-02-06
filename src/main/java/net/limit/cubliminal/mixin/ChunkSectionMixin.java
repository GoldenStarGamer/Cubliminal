package net.limit.cubliminal.mixin;

import net.limit.cubliminal.util.ChunkSectionAccessor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin implements ChunkSectionAccessor {
    @Shadow
    private ReadableContainer<RegistryEntry<Biome>> biomeContainer;

    @Unique
    public void cubliminal$populateBiomes(RegistryEntry<Biome> biome) {
        PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.slice();

        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 4; ++l) {
                    palettedContainer.swapUnsafe(j, k, l, biome);
                }
            }
        }

        this.biomeContainer = palettedContainer;
    }

}
