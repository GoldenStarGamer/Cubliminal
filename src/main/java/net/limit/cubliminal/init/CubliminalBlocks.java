package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.CustomProperties;
import net.limit.cubliminal.block.custom.*;
import net.limit.cubliminal.item.AlmondWaterBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.function.ToIntFunction;

import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

public class CubliminalBlocks {
    private static void registerBlockItem(String id, Block block) {
        Registry.register(Registries.ITEM, Cubliminal.id(id),
                new BlockItem(block, new Item.Settings()));
    }
    private static Block registerBlock(String id, Block block) {
        registerBlockItem(id, block);
		return Registry.register(Registries.BLOCK, Cubliminal.id(id), block);
    }
	private static Block registerBlockWithItem(String id, Block block, FoodComponent foodComponent, int maxCount) {
		Registry.register(Registries.ITEM, Cubliminal.id(id),
				new AlmondWaterBlockItem(block, new Item.Settings().food(foodComponent).maxCount(maxCount)));
		return Registry.register(Registries.BLOCK, Cubliminal.id(id), block);
	}


    public static final Block YELLOW_WALLPAPERS = registerBlock("yellow_wallpapers",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool()));

    public static final Block YELLOW_WALLPAPERS_WALL = registerBlock("yellow_wallpapers_wall",
			new WallBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool()));

	public static final Block YELLOW_WALLPAPERS_VERTICAL_SLAB = registerBlock("yellow_wallpapers_vertical_slab",
			new VerticalSlabBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_YELLOW)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool()));

    public static final Block DAMAGED_YELLOW_WALLPAPERS = registerBlock("damaged_yellow_wallpapers",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(2, 6)));

    public static final Block BOTTOM_YELLOW_WALLPAPERS = registerBlock("bottom_yellow_wallpapers",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_YELLOW)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool()));

    public static final Block FALSE_CEILING = registerBlock("false_ceiling",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.LIGHT_GRAY)
					.sounds(BlockSoundGroup.CALCITE)
					.strength(2, 6)
					.requiresTool()));

    public static final Block DAMP_CARPET = registerBlock("damp_carpet",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.OAK_TAN)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3)));

    public static final Block DIRTY_DAMP_CARPET = registerBlock("dirty_damp_carpet",
            new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.OAK_TAN)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3)));

	public static final Block RED_WALLPAPERS = registerBlock("red_wallpapers",
			new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_RED)
					.sounds(BlockSoundGroup.BASALT)
					.strength(5, 7)
					.requiresTool()));

	public static final Block RED_DAMP_CARPET = registerBlock("red_damp_carpet",
			new Block(AbstractBlock.Settings.create()
					.mapColor(MapColor.RED)
					.sounds(BlockSoundGroup.WOOL)
					.strength(1, 3)
					.slipperiness(0.7f)));

	public static final Block FLICKERING_FLUORESCENT_LIGHT = registerBlock("fluorescent_light",
			new FluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(shouldBeRed(15, 8))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.emissiveLighting(Blocks::always)
					.requiresTool()));

	public static final Block FLUORESCENT_LIGHT = registerBlock("deco_fluorescent_light",
			new FluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.luminance(shouldBeRed(15, 8))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.emissiveLighting(Blocks::always)
					.requiresTool()));

	public static final Block FUSED_FLUORESCENT_LIGHT = registerBlock("fused_fluorescent_light",
			new FusedFluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(shouldBeRed(6, 4))
				.sounds(BlockSoundGroup.GLASS)
					.nonOpaque()
					.requiresTool()));

	public static final Block SMOKE_DETECTOR = registerBlock("smoke_detector",
			new SmokeDetectorBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.DEEPSLATE_GRAY)
				.strength(2.6f, 2.6f)
				.offset(AbstractBlock.OffsetType.XZ)
				.dynamicBounds()
				.sounds(BlockSoundGroup.METAL)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool()));

	public static final Block SOCKET = registerBlock("socket",
			new SocketBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE_GRAY)
				.strength(3, 3)
				.sounds(BlockSoundGroup.CALCITE)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool()));

	public static final Block ALMOND_WATER = registerBlockWithItem("almond_water",
			new AlmondWaterBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.breakInstantly()
					.dynamicBounds()
					.offset(AbstractBlock.OffsetType.XZ)
					.sounds(BlockSoundGroup.LANTERN)
					.pistonBehavior(PistonBehavior.DESTROY)), CubliminalFoodComponents.ALMOND_WATER, 16);

	public static final Block JUMBLED_DOCUMENTS = registerBlock("jumbled_documents",
			new JumbledDocumentsBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.WHITE)
					.breakInstantly()
					.sounds(new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN,
							SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundEvents.ITEM_BOOK_PAGE_TURN))
					.nonOpaque()
					.noCollision()
					.noBlockBreakParticles()
					.pistonBehavior(PistonBehavior.DESTROY)));

	public static final Block TWO_LONG_SPRUCE_TABLE = registerBlock("two_long_spruce_table",
			new TwoLongTableBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)
					.requiresTool()));

	public static final Block SPRUCE_CHAIR = registerBlock("spruce_chair",
			new ChairBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)
					.requiresTool()));

	public static final Block MANILA_WALLPAPERS = registerBlock("manila_wallpapers",
			new Block(AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool()));

	public static final Block TOP_MANILA_WALLPAPERS = registerBlock("top_manila_wallpapers",
			new Block(AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.sounds(BlockSoundGroup.BASALT)
				.strength(5, 7)
				.requiresTool()));

	public static final Block EMERGENCY_EXIT_DOOR_0 = registerBlock("emergency_exit_door_0",
			new DoorBlock(BlockSetType.IRON,
				AbstractBlock.Settings.create()
						.mapColor(MapColor.RED)
						.strength(5.0f)
						.nonOpaque()
						.pistonBehavior(PistonBehavior.DESTROY)
						.requiresTool()));

	public static final Block EXIT_SIGN = registerBlock("exit_sign",
			new ExitSignBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_GREEN)
					.strength(3, 3)
					.sounds(BlockSoundGroup.CALCITE)
					.pistonBehavior(PistonBehavior.DESTROY)
					.requiresTool()));

	public static final Block COMPUTER = registerBlock("computer",
			new ComputerBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.strength(4, 5)
					.sounds(BlockSoundGroup.GLASS)
					.dropsNothing()
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK)));

	public static final Block SINK = registerBlock("sink",
			new SinkBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_YELLOW)
					.strength(4, 5)
					.sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK)
					.requiresTool()));

	public static final Block SHOWER = registerBlock("shower",
			new ShowerBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.DEEPSLATE_GRAY)
					.strength(4, 5)
					.sounds(BlockSoundGroup.COPPER)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.BLOCK)
					.requiresTool()));

	public static final Block THE_LOBBY_GATEWAY_BLOCK = registerBlock("the_lobby_gateway_block",
			new TheLobbyGatewayBlock(AbstractBlock.Settings.copy(Blocks.GLASS)
					.strength(-1, 3600000)
					.noCollision()
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.noBlockBreakParticles()
					.luminance(createLightLevelFromLitBlockState(9))));

	public static final Block GABBRO = registerBlock("gabbro",
			new Block(AbstractBlock.Settings.copy(Blocks.STONE)
					.mapColor(MapColor.BLACK)
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.strength(-1, 3600000)));

	public static final Block MOLD = registerBlock("mold",
			new MoldBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.BLACK)
					.replaceable()
					.noCollision()
					.strength(0.2f)
					.sounds(BlockSoundGroup.GLOW_LICHEN)
					.burnable()
					.pistonBehavior(PistonBehavior.DESTROY)));

	public static final Block POOL_TILES = registerBlock("pool_tiles",
			new Block(AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE)
					.sounds(BlockSoundGroup.DEEPSLATE_TILES)));

	public static final Block POOL_TILE_STAIRS = registerBlock("pool_tile_stairs",
			new StairsBlock(CubliminalBlocks.POOL_TILES.getDefaultState(),
					AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE)));

	public static final Block POOL_TILE_SLAB = registerBlock("pool_tile_slab",
			new SlabBlock(AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE)));

	public static final Block POOL_TILE_WALL = registerBlock("pool_tile_wall",
			new WallBlock(AbstractBlock.Settings.copy(Blocks.REINFORCED_DEEPSLATE)));


	public static boolean isRed(BlockState state, BlockView world, BlockPos pos) {
		return state.get(CustomProperties.RED);
	}

	public static ToIntFunction<BlockState> shouldBeRed(int defaultLevel, int redLevel) {
		return (state) -> {
			int litLevel = state.get(CustomProperties.RED) ? redLevel : defaultLevel;
			return (Boolean) state.get(Properties.LIT) ? litLevel : 0;
		};
	}



    public static void init() {
		FuelRegistry.INSTANCE.add(YELLOW_WALLPAPERS.asItem(), 300);
		FuelRegistry.INSTANCE.add(YELLOW_WALLPAPERS_WALL.asItem(), 300);
		FuelRegistry.INSTANCE.add(YELLOW_WALLPAPERS_VERTICAL_SLAB.asItem(), 300);
		FuelRegistry.INSTANCE.add(BOTTOM_YELLOW_WALLPAPERS.asItem(), 300);
		FuelRegistry.INSTANCE.add(DAMAGED_YELLOW_WALLPAPERS.asItem(), 200);
		FuelRegistry.INSTANCE.add(MANILA_WALLPAPERS.asItem(), 300);
		FuelRegistry.INSTANCE.add(TOP_MANILA_WALLPAPERS.asItem(), 300);
		FuelRegistry.INSTANCE.add(TWO_LONG_SPRUCE_TABLE.asItem(), 1000);
		FuelRegistry.INSTANCE.add(SPRUCE_CHAIR.asItem(), 800);
		FuelRegistry.INSTANCE.add(DAMP_CARPET.asItem(), 100);
		FuelRegistry.INSTANCE.add(DIRTY_DAMP_CARPET.asItem(), 100);
		FuelRegistry.INSTANCE.add(RED_DAMP_CARPET.asItem(), 100);
		FuelRegistry.INSTANCE.add(RED_WALLPAPERS.asItem(), 300);
    }
}
