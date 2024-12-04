package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.CustomProperties;
import net.limit.cubliminal.block.custom.*;
import net.limit.cubliminal.item.AlmondWaterBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

public class CubliminalBlocks {

	private static Block register(String id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSettings, BiFunction<Block, Item.Settings, BlockItem> itemFactory, Item.Settings itemSettings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Cubliminal.id(id));
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Cubliminal.id(id));

		Block block = blockFactory.apply(blockSettings.registryKey(blockKey));
		BlockItem item = itemFactory.apply(block, itemSettings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return Registry.register(Registries.BLOCK, blockKey, block);
	}

	private static <T> Block register(String id, BiFunction<T, AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSettings, T constructorData, BiFunction<Block, Item.Settings, BlockItem> itemFactory, Item.Settings itemSettings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Cubliminal.id(id));
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Cubliminal.id(id));

		Block block = blockFactory.apply(constructorData, blockSettings.registryKey(blockKey));
		BlockItem item = itemFactory.apply(block, itemSettings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return Registry.register(Registries.BLOCK, blockKey, block);
	}

	private static Block register(String id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSettings) {
		return register(id, blockFactory, blockSettings, BlockItem::new, new Item.Settings());
	}



    public static final Block YELLOW_WALLPAPERS = register("yellow_wallpapers", Block::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool());

    public static final Block YELLOW_WALLPAPERS_WALL = register("yellow_wallpapers_wall", WallBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool()
					.solid());

	public static final Block YELLOW_WALLPAPERS_VERTICAL_SLAB = register("yellow_wallpapers_vertical_slab", VerticalSlabBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_YELLOW)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool());

    public static final Block DAMAGED_YELLOW_WALLPAPERS = register("damaged_yellow_wallpapers", Block::new,
            AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(2, 6));

    public static final Block BOTTOM_YELLOW_WALLPAPERS = register("bottom_yellow_wallpapers", Block::new,
            AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool());

    public static final Block FALSE_CEILING = register("false_ceiling", Block::new,
            AbstractBlock.Settings.create()
					.mapColor(MapColor.LIGHT_GRAY)
					.sounds(BlockSoundGroup.CALCITE)
					.strength(2, 6)
					.requiresTool());

    public static final Block DAMP_CARPET = register("damp_carpet", Block::new,
            AbstractBlock.Settings.create()
					.mapColor(MapColor.OAK_TAN)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3));

    public static final Block DIRTY_DAMP_CARPET = register("dirty_damp_carpet", Block::new,
            AbstractBlock.Settings.create()
					.mapColor(MapColor.OAK_TAN)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3));

	public static final Block RED_WALLPAPERS = register("red_wallpapers", Block::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_RED)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool());

	public static final Block RED_DAMP_CARPET = register("red_damp_carpet", Block::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.RED)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3)
					.slipperiness(0.7f));

	public static final Block FLICKERING_FLUORESCENT_LIGHT = register("fluorescent_light", FluorescentLightBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(shouldBeRed(15, 8))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.emissiveLighting(Blocks::always)
					.requiresTool());

	public static final Block FLUORESCENT_LIGHT = register("deco_fluorescent_light", FluorescentLightBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.luminance(shouldBeRed(15, 8))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.emissiveLighting(Blocks::always)
					.requiresTool());

	public static final Block FUSED_FLUORESCENT_LIGHT = register("fused_fluorescent_light", FusedFluorescentLightBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(shouldBeRed(6, 4))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.requiresTool());

	public static final Block SMOKE_DETECTOR = register("smoke_detector", SmokeDetectorBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DEEPSLATE_GRAY)
				.strength(2.6f, 2.6f)
				.offset(AbstractBlock.OffsetType.XZ)
				.dynamicBounds()
				.sounds(BlockSoundGroup.METAL)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool());

	public static final Block SOCKET = register("socket", SocketBlock::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE_GRAY)
				.strength(3, 3)
				.sounds(BlockSoundGroup.CALCITE)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool());

	public static final Block ALMOND_WATER = register("almond_water", AlmondWaterBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.breakInstantly()
					.dynamicBounds()
					.offset(AbstractBlock.OffsetType.XZ)
					.sounds(BlockSoundGroup.LANTERN)
					.pistonBehavior(PistonBehavior.DESTROY),
			AlmondWaterBlockItem::new, new Item.Settings()
					.food(CubliminalFoodComponents.ALMOND_WATER)
					.maxCount(16)
					.component(DataComponentTypes.CONSUMABLE, CubliminalFoodComponents.ALMOND_WATER_COMPONENT));

	public static final Block JUMBLED_DOCUMENTS = register("jumbled_documents", JumbledDocumentsBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.WHITE)
					.breakInstantly()
					.sounds(new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN,
							SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN))
					.nonOpaque()
					.noCollision()
					.noBlockBreakParticles()
					.pistonBehavior(PistonBehavior.DESTROY));

	public static final Block TWO_LONG_SPRUCE_TABLE = register("two_long_spruce_table", TwoLongTableBlock::new,
			AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)
					.requiresTool());

	public static final Block SPRUCE_CHAIR = register("spruce_chair", ChairBlock::new,
			AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)
					.requiresTool());

	public static final Block MANILA_WALLPAPERS = register("manila_wallpapers", Block::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool());

	public static final Block TOP_MANILA_WALLPAPERS = register("top_manila_wallpapers", Block::new,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool());

	public static final Block EMERGENCY_EXIT_DOOR_0 = register("emergency_exit_door_0", DoorBlock::new,
				AbstractBlock.Settings.create()
						.mapColor(MapColor.RED)
						.strength(5.0f)
						.nonOpaque()
						.pistonBehavior(PistonBehavior.DESTROY)
						.requiresTool(),
			BlockSetType.IRON, BlockItem::new, new Item.Settings());

	public static final Block EXIT_SIGN = register("exit_sign", ExitSignBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_GREEN)
					.strength(3, 3)
					.sounds(BlockSoundGroup.CALCITE)
					.pistonBehavior(PistonBehavior.DESTROY)
					.requiresTool());

	public static final Block COMPUTER = register("computer", ComputerBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.strength(4, 5)
					.sounds(BlockSoundGroup.GLASS)
					.dropsNothing()
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK));

	public static final Block SINK = register("sink", SinkBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.strength(4, 5)
					.sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK)
					.requiresTool());

	public static final Block SHOWER = register("shower", ShowerBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.DEEPSLATE_GRAY)
					.strength(4, 5)
					.sounds(BlockSoundGroup.COPPER)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK)
					.requiresTool());

	public static final Block THE_LOBBY_GATEWAY_BLOCK = register("the_lobby_gateway_block", TheLobbyGatewayBlock::new,
			AbstractBlock.Settings.copy(Blocks.GLASS)
					.strength(-1, 3600000)
					.noCollision()
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.noBlockBreakParticles()
					.luminance(createLightLevelFromLitBlockState(9)));

	public static final Block GABBRO = register("gabbro", Block::new,
			AbstractBlock.Settings.copy(Blocks.STONE)
					.mapColor(MapColor.BLACK)
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.strength(-1, 3600000));

	public static final Block MOLD = register("mold", MoldBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.BLACK)
					.replaceable()
					.noCollision()
					.strength(0.2f)
					.sounds(BlockSoundGroup.GLOW_LICHEN)
					.burnable()
					.pistonBehavior(PistonBehavior.DESTROY));

	public static final Block POOL_TILES = register("pool_tiles", Block::new,
			AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE)
					.sounds(BlockSoundGroup.DEEPSLATE_TILES));

	public static final Block POOL_TILE_STAIRS = register("pool_tile_stairs", StairsBlock::new,
					AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE),
			CubliminalBlocks.POOL_TILES.getDefaultState(), BlockItem::new, new Item.Settings());

	public static final Block POOL_TILE_SLAB = register("pool_tile_slab", SlabBlock::new,
			AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE));

	public static final Block POOL_TILE_WALL = register("pool_tile_wall", WallBlock::new,
			AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE).solid());


	public static ToIntFunction<BlockState> shouldBeRed(int defaultLevel, int redLevel) {
		return (state) -> {
			int litLevel = state.get(CustomProperties.RED) ? redLevel : defaultLevel;
			return (Boolean) state.get(Properties.LIT) ? litLevel : 0;
		};
	}



    public static void init() {
		FuelRegistryEvents.BUILD.register((builder, context) -> {
			builder.add(YELLOW_WALLPAPERS.asItem(), 300);
			builder.add(YELLOW_WALLPAPERS_WALL.asItem(), 300);
			builder.add(YELLOW_WALLPAPERS_VERTICAL_SLAB.asItem(), 300);
			builder.add(BOTTOM_YELLOW_WALLPAPERS.asItem(), 300);
			builder.add(DAMAGED_YELLOW_WALLPAPERS.asItem(), 200);
			builder.add(MANILA_WALLPAPERS.asItem(), 300);
			builder.add(TOP_MANILA_WALLPAPERS.asItem(), 300);
			builder.add(TWO_LONG_SPRUCE_TABLE.asItem(), 1000);
			builder.add(SPRUCE_CHAIR.asItem(), 800);
			builder.add(DAMP_CARPET.asItem(), 100);
			builder.add(DIRTY_DAMP_CARPET.asItem(), 100);
			builder.add(RED_DAMP_CARPET.asItem(), 100);
			builder.add(RED_WALLPAPERS.asItem(), 300);
		});
    }
}
