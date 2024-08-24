package net.limit.cubliminal.block.entity;

import net.limit.cubliminal.block.custom.ShowerBlock;
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

public class ShowerBlockEntity extends BlockEntity {
	private long age;

	public ShowerBlockEntity(BlockPos pos, BlockState state) {
		super(CubliminalBlockEntities.SHOWER_BLOCK_ENTITY, pos, state);
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

	public static void tick(World world, BlockPos pos, BlockState state, ShowerBlockEntity blockEntity) {
		++blockEntity.age;
		if (state.get(Properties.ENABLED)) {
			float offsetX = 0;
			float offsetZ = 0;
			switch (state.get(ShowerBlock.FACING)) {
				case NORTH:
					offsetX = 0.375f;
					offsetZ = 0.5f;
					break;
				case SOUTH:
					offsetX = 0.375f;
					offsetZ = 0.25f;
					break;
				case WEST:
					offsetX = 0.5f;
					offsetZ = 0.375f;
					break;
				case EAST:
					offsetX = 0.25f;
					offsetZ = 0.375f;
					break;
			}
			for (float x = 0; x <= 0.25f; x += 0.125f) {
				for (float z = 0; z <= 0.25f; z += 0.125f) {
					world.addParticle(ParticleTypes.FALLING_WATER, pos.getX() + x + offsetX,
						pos.getY() + 1.65, pos.getZ() + z + offsetZ, 0, 0, 0);
				}
			}
			if (blockEntity.age % 12 == 0) {
				CubliminalSounds.blockPlaySound(world, pos, CubliminalSounds.SINK_AMBIENT.value());
			}
		}
	}
}
