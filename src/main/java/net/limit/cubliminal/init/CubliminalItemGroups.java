package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.limit.cubliminal.Cubliminal;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CubliminalItemGroups {
    public static final ItemGroup BACKROOMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Cubliminal.id("backrooms"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.Backrooms"))
                    .icon(() -> new ItemStack(CubliminalBlocks.YELLOW_WALLPAPERS)).entries((displayContext, entries) -> {

                        entries.add(CubliminalItems.YELLOW_WALLPAPER);
                        entries.add(CubliminalBlocks.YELLOW_WALLPAPERS);
                        entries.add(CubliminalBlocks.YELLOW_WALLPAPERS_WALL);
						entries.add(CubliminalBlocks.YELLOW_WALLPAPERS_VERTICAL_SLAB);
                        entries.add(CubliminalBlocks.BOTTOM_YELLOW_WALLPAPERS);
                        entries.add(CubliminalBlocks.DAMAGED_YELLOW_WALLPAPERS);
                        entries.add(CubliminalBlocks.FALSE_CEILING);
                        entries.add(CubliminalBlocks.DAMP_CARPET);
                        entries.add(CubliminalBlocks.DIRTY_DAMP_CARPET);
						entries.add(CubliminalBlocks.FLICKERING_FLUORESCENT_LIGHT);
						entries.add(CubliminalBlocks.FLUORESCENT_LIGHT);
						entries.add(CubliminalBlocks.FUSED_FLUORESCENT_LIGHT);
						entries.add(CubliminalBlocks.MANILA_WALLPAPERS);
						entries.add(CubliminalBlocks.TOP_MANILA_WALLPAPERS);
						entries.add(CubliminalBlocks.EMERGENCY_EXIT_DOOR_0);
						entries.add(CubliminalBlocks.EXIT_SIGN);
						entries.add(CubliminalBlocks.GABBRO);
						entries.add(CubliminalBlocks.SMOKE_DETECTOR);
						entries.add(CubliminalBlocks.ELECTRICAL_PLUG);
						entries.add(CubliminalBlocks.TWO_LONG_SPRUCE_TABLE);
                        entries.add(CubliminalBlocks.SPRUCE_CHAIR);
						entries.add(CubliminalBlocks.SINK);
						entries.add(CubliminalBlocks.SHOWER);
                        entries.add(CubliminalItems.ALMOND_WATER);
                        entries.add(CubliminalItems.WOODEN_PLANK);
                        entries.add(CubliminalItems.NAILED_BAT);

                    }).build());

    public static void init() {
    }
}
