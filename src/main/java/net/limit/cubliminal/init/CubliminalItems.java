package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.item.AlmondWaterItem;
import net.limit.cubliminal.item.BasicWeaponItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CubliminalItems {

    public static final Item YELLOW_WALLPAPER = registerItem("yellow_wallpaper", new Item(new FabricItemSettings()));
    public static final Item CRIMSON_WALLPAPER = registerItem("crimson_wallpaper", new Item(new FabricItemSettings()));
    public static final Item ALMOND_WATER = registerItem("almond_water", new AlmondWaterItem(new FabricItemSettings().food(CubliminalFoodComponents.ALMOND_WATER).maxCount(16)));
    public static final Item WOODEN_PLANK = registerItem("wooden_plank", new BasicWeaponItem(2, -2f, new FabricItemSettings()));
    public static final Item NAILED_BAT = registerItem("nailed_bat", new SwordItem(ToolMaterials.IRON, 3, -2.2f, new FabricItemSettings().maxCount(1)));


    private static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, Cubliminal.id(id), item);
    }

    public static void init() {
        FuelRegistry.INSTANCE.add(WOODEN_PLANK.asItem(), 100);
	}
}
