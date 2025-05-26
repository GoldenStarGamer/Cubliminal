package net.limit.cubliminal.mixin;

import net.limit.cubliminal.access.ChunkAccessor;
import net.limit.cubliminal.access.ChunkSectionAccessor;
import net.limit.cubliminal.world.biome.source.LiminalBiomeSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ChunkAccessor {

    @Shadow
    public abstract ChunkPos getPos();

    @Shadow
    public abstract HeightLimitView getHeightLimitView();

    @Shadow
    public abstract ChunkSection getSection(int yIndex);

    @Unique
    public void cubliminal$populateBiomes(BiomeSupplier biomeSupplier) {
        if (biomeSupplier instanceof LiminalBiomeSource biomeSource) {
            ChunkPos chunkPos = this.getPos();
            HeightLimitView heightLimitView = this.getHeightLimitView();
            int k = heightLimitView.getBottomSectionCoord();

            RegistryEntry<Biome> biome = biomeSource.calcBiome(chunkPos.getStartX(), k, chunkPos.getStartZ());

            for (; k <= heightLimitView.getTopSectionCoord(); ++k) {
                ChunkSection chunkSection = this.getSection(this.getHeightLimitView().sectionCoordToIndex(k));
                ((ChunkSectionAccessor) chunkSection).cubliminal$populateBiomes(biome);
            }
        }
    }
}
