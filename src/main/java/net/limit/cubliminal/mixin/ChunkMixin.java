package net.limit.cubliminal.mixin;

import net.limit.cubliminal.util.ChunkAccessor;
import net.limit.cubliminal.util.ChunkSectionAccessor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
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
    public void cubliminal$populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
        ChunkPos chunkPos = this.getPos();
        int i = BiomeCoords.fromBlock(chunkPos.getStartX());
        int j = BiomeCoords.fromBlock(chunkPos.getStartZ());
        HeightLimitView heightLimitView = this.getHeightLimitView();

        int k = heightLimitView.getBottomSectionCoord();

        RegistryEntry<Biome> biome = biomeSupplier.getBiome(i, BiomeCoords.fromChunk(k), j, sampler);

        for (; k <= heightLimitView.getTopSectionCoord(); ++k) {
            ChunkSection chunkSection = this.getSection(this.getHeightLimitView().sectionCoordToIndex(k));
            ((ChunkSectionAccessor) chunkSection).cubliminal$populateBiomes(biome);
        }

    }
}
