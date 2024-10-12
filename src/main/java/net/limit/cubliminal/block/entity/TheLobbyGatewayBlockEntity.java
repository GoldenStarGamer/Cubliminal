package net.limit.cubliminal.block.entity;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.advancements.AdvancementHelper;
import net.limit.cubliminal.init.CubliminalBlockEntities;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.limit.cubliminal.util.SanityData;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class TheLobbyGatewayBlockEntity extends BlockEntity {

	public TheLobbyGatewayBlockEntity(BlockPos pos, BlockState state) {
		super(CubliminalBlockEntities.THE_LOBBY_GATEWAY_BLOCK_ENTITY, pos, state);
        int theLobbyLayers = 3;
        int theLobbyCellHeight = 7;
        //this.exitPos = new BlockPos(7, theLobbyLayers * theLobbyCellHeight + 2, 3);
	}

	private long age;
	private BlockPos exitPos = new BlockPos(7, 23, 3);

	public void writeExitPos(BlockPos blockPos) {
		this.exitPos = blockPos;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putLong("Age", this.age);
		nbt.put("exitPos", NbtHelper.fromBlockPos(exitPos));
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.age = nbt.getLong("Age");
		BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("exitPos"));
		if (World.isValid(blockPos)) {
			this.exitPos = blockPos;
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, TheLobbyGatewayBlockEntity blockEntity) {
		if (!world.isClient) {
			++blockEntity.age;
			if (blockEntity.getWorld().getRegistryKey().equals(CubliminalRegistrar.THE_LOBBY_KEY)) {
				if (world.getBlockEntity(pos.down()) instanceof TheLobbyGatewayBlockEntity) return;
				if (blockEntity.age % 100 == 0 && state.equals(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK
					.getDefaultState().with(Properties.LIT, false))) {

					for (Entity entity : world.getEntitiesByClass(Entity.class, new Box(pos).expand(16, 2, 11), Entity::isPlayer)) {
						SanityData.resetTimer((ServerPlayerEntity) entity);
					}
				}
				List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(pos), TheLobbyGatewayBlockEntity::canTeleport);
				if (!list.isEmpty()) tryTeleportingEntity(world, pos, state, list.get(world.random.nextInt(list.size())), blockEntity);
			}
		}
	}

	public static void tryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, TheLobbyGatewayBlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			Entity entity3;
			if (entity instanceof EnderPearlEntity) {
				Entity entity2 = ((EnderPearlEntity) entity).getOwner();
				if (entity2 instanceof ServerPlayerEntity) {
					Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity) entity2, state);
				}

				if (entity2 != null) {
					entity3 = entity2;
					entity.discard();
				} else {
					entity3 = entity;
				}
			} else {
				entity3 = entity.getRootVehicle();
			}

			entity3.resetPortalCooldown();
			entity3.teleport((double) blockEntity.exitPos.getX() + 0.5, blockEntity.exitPos.getY(), (double) blockEntity.exitPos.getZ() + 0.5);
			if (entity3.isPlayer()) {
				AdvancementHelper.grantAdvancement((ServerPlayerEntity) entity3, Cubliminal.id("backrooms/manila_room"));
			}
			if (state.equals(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK.getDefaultState().with(Properties.LIT, true))) {
				for (BlockEntity blockEntity2 : BlockPos.stream(new Box(blockEntity.exitPos).expand(10))
					.map(blockEntity.getWorld()::getBlockEntity).filter(Objects::nonNull).filter(blockEntity2 ->
						blockEntity2.getCachedState().equals(CubliminalBlocks.THE_LOBBY_GATEWAY_BLOCK
							.getDefaultState().with(Properties.LIT, false))).toList()) {
					((TheLobbyGatewayBlockEntity) blockEntity2).writeExitPos(pos.add(3, 0, 0));
				}
			}
		}
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			return true;
		} else return super.onSyncedBlockEvent(type, data);
	}

	public static boolean canTeleport(Entity entity) {
		return EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !entity.getRootVehicle().hasPortalCooldown();
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}
}
