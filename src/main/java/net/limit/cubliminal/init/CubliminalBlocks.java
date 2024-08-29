package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.custom.*;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;

import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

public class CubliminalBlocks {
    private static void registerBlockItem(String id, Block block) {
        Registry.register(Registries.ITEM, Cubliminal.id(id),
                new BlockItem(block, new FabricItemSettings()));
    }
    private static Block registerBlock(String id, Block block) {
        registerBlockItem(id, block);
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

	public static final Block FLICKERING_FLUORESCENT_LIGHT = registerBlock("fluorescent_light",
			new FluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(createLightLevelFromLitBlockState(15))
				.sounds(BlockSoundGroup.GLASS)
					.requiresTool()));

	public static final Block FLUORESCENT_LIGHT = registerBlock("deco_fluorescent_light",
			new FluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE)
				.strength(1, 2)
				.luminance(createLightLevelFromLitBlockState(15))
				.sounds(BlockSoundGroup.GLASS)
					.requiresTool()));

	public static final Block FUSED_FLUORESCENT_LIGHT = registerBlock("fused_fluorescent_light",
			new FusedFluorescentLightBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.strength(1, 2)
				.ticksRandomly()
				.luminance(createLightLevelFromLitBlockState(6))
				.sounds(BlockSoundGroup.GLASS)
					.requiresTool()));

	public static final Block SMOKE_DETECTOR = registerBlock("smoke_detector",
			new SmokeDetectorBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.DEEPSLATE_GRAY)
				.strength(2.6f, 2.6f)
				.offset(AbstractBlock.OffsetType.XZ)
				.dynamicBounds()
				.sounds(BlockSoundGroup.LANTERN)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool()));

	public static final Block ELECTRICAL_PLUG = registerBlock("electrical_plug",
			new ElectricalPlugBlock(AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE_GRAY)
				.strength(3, 3)
				.sounds(BlockSoundGroup.CALCITE)
				.pistonBehavior(PistonBehavior.DESTROY)
				.requiresTool()));

	public static final Block TWO_LONG_SPRUCE_TABLE = registerBlock("two_long_spruce_table",
			new TwoLongTableBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)
					.requiresTool()));

	public static final Block SPRUCE_CHAIR = registerBlock("spruce_chair",
			new ChairBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)
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
			new TheLobbyGatewayBlock(FabricBlockSettings.copyOf(Blocks.GLASS)
					.strength(-1, 3600000)
					.noCollision()
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.noBlockBreakParticles()
					.luminance(createLightLevelFromLitBlockState(9))));

	public static final Block GABBRO = registerBlock("gabbro",
			new Block(FabricBlockSettings.copyOf(Blocks.STONE)
					.mapColor(MapColor.BLACK)
					.dropsNothing()
					.pistonBehavior(PistonBehavior.BLOCK)
					.strength(-1, 3600000)));

	public static final Block POOL_TILES = registerBlock("pool_tiles",
			new Block(FabricBlockSettings.copyOf(Blocks.REINFORCED_DEEPSLATE)
					.sounds(BlockSoundGroup.DEEPSLATE_TILES)));

	public static final Block POOL_TILE_STAIRS = registerBlock("pool_tile_stairs",
			new StairsBlock(CubliminalBlocks.POOL_TILES.getDefaultState(),
					FabricBlockSettings.copyOf(Blocks.REINFORCED_DEEPSLATE)));

	public static final Block POOL_TILE_SLAB = registerBlock("pool_tile_slab",
			new SlabBlock(FabricBlockSettings.copyOf(Blocks.REINFORCED_DEEPSLATE)));

	public static final Block POOL_TILE_WALL = registerBlock("pool_tile_wall",
			new WallBlock(FabricBlockSettings.copyOf(Blocks.REINFORCED_DEEPSLATE)));



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
    }
}
