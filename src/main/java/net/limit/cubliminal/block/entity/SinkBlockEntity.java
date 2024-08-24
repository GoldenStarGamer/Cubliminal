package net.limit.cubliminal.block.entity;

import net.limit.cubliminal.block.custom.SinkBlock;
import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.limit.cubliminal.init.CubliminalSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SinkBlockEntity extends BlockEntity {
	private long age;

	public SinkBlockEntity(BlockPos pos, BlockState state) {
		super(CubliminalBlockEntities.SINK_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putLong("Age", this.age);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.age = nbt.getLong("Age");
	}

	public static void tick(World world, BlockPos pos, BlockState state, SinkBlockEntity blockEntity) {
		++blockEntity.age;
		if (state.get(Properties.ENABLED)) {
			switch (state.get(SinkBlock.FACING)) {
				case NORTH:
					world.addParticle(ParticleTypes.FALLING_WATER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.68, 0, 0, 0);
					break;
				case SOUTH:
					world.addParticle(ParticleTypes.FALLING_WATER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.32, 0, 0, 0);
					break;
				case WEST:
					world.addParticle(ParticleTypes.FALLING_WATER, pos.getX() + 0.68, pos.getY() + 1.05, pos.getZ() + 0.5, 0, 0, 0);
					break;
				case EAST:
					world.addParticle(ParticleTypes.FALLING_WATER, pos.getX() + 0.32, pos.getY() + 1.05, pos.getZ() + 0.5, 0, 0, 0);
					break;
			}
			if (blockEntity.age % 12 == 0) {
				CubliminalSounds.blockPlaySound(world, pos, CubliminalSounds.SINK_AMBIENT.value());
			}
		}
	}
}
