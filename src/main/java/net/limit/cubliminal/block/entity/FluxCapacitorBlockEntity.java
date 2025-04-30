package net.limit.cubliminal.block.entity;

import net.limit.cubliminal.block.custom.FluxCapacitorBlock;
import net.limit.cubliminal.client.hud.NoClippingHudOverlay;
import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.limit.cubliminal.init.CubliminalSounds;
import net.limit.cubliminal.event.noclip.NoClipEngine;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class FluxCapacitorBlockEntity extends BlockEntity {

	public FluxCapacitorBlockEntity(BlockPos pos, BlockState state) {
		super(CubliminalBlockEntities.FLUX_CAPACITOR_BLOCK_ENTITY, pos, state);
	}

	private boolean canBreakReality;
	private int realityTicks;


	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putInt("RealityTicks", this.realityTicks);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.realityTicks = nbt.getInt("RealityTicks");
	}


	public static void tick(World world, BlockPos pos, BlockState state, FluxCapacitorBlockEntity entity) {
		if (state.get(FluxCapacitorBlock.POWERED) && !entity.canBreakReality) {
			entity.canBreakReality = true;
			world.playSoundAtBlockCenter(pos, CubliminalSounds.FLUX_CAPACITOR.value(),
					SoundCategory.BLOCKS, 1.0f, 1.0f, false);
		}
		if (entity.canBreakReality) {
			entity.breakReality(world, state);
		}
	}


	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			return true;
		} else return super.onSyncedBlockEvent(type, data);
	}

	public void breakReality(World world, BlockState state) {
		if (this.realityTicks > 279) {
			world.setBlockState(pos, state.with(FluxCapacitorBlock.POWERED, false));
			this.canBreakReality = false;
			this.realityTicks = 0;
		} else {
			++this.realityTicks;
			if (this.realityTicks == 220) {
				if (world.isClient) {
					NoClippingHudOverlay.INSTANCE.setAux_renderOverlay(false);
				} else {
					for (PlayerEntity player : world.getPlayers().stream().filter(Predicate.not(PlayerEntity::isSpectator)).toList()) {
						NoClipEngine.noClip(player);
					}
				}
			} else if (world.isClient && this.realityTicks == 100 &&
					!MinecraftClient.getInstance().player.isSpectator()) {
				NoClippingHudOverlay.INSTANCE.setAux_renderOverlay(true);
			}
		}
	}

	public int getRealityTicks() {
		return this.realityTicks;
	}

	public boolean canBreakReality() {
		return this.canBreakReality;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createNbt(registryLookup);
	}
}
