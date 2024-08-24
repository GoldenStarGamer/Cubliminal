package net.limit.cubliminal.block.entity;

import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FluorescentLightBlockEntity extends BlockEntity {
	private long age;
	//private long flickerTicks;

	public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
		super(CubliminalBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putLong("Age", this.age);
		//nbt.putLong("FlickerTicks", this.flickerTicks);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.age = nbt.getLong("Age");
		//this.flickerTicks = nbt.getLong("FlickerTicks");
	}

	public static void tick(World world, BlockPos pos, BlockState state, FluorescentLightBlockEntity blockEntity) {
		if (!world.isClient) {
			++blockEntity.age;
			if (blockEntity.age % 400 == 0) {
				if (new Random().nextInt(4) == 0) {
					//blockEntity.flickerTicks += new Random().nextLong(3, 6) * 400;
					world.setBlockState(pos, state.with(Properties.LIT, false));
					world.scheduleBlockTick(pos, state.getBlock(), 2);
				}
			}
		}
	}
}
