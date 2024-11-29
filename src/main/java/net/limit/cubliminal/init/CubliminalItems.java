package net.limit.cubliminal.init;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.limit.cubliminal.Cubliminal;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static net.minecraft.item.Item.ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.ATTACK_SPEED_MODIFIER_ID;

public class CubliminalItems {

    public static final Item YELLOW_WALLPAPER = registerItem("yellow_wallpaper", new Item(new Item.Settings()));
    public static final Item CRIMSON_WALLPAPER = registerItem("crimson_wallpaper", new Item(new Item.Settings()));
    public static final Item WOODEN_PLANK = registerItem("wooden_plank", new Item(new Item.Settings().attributeModifiers(
            AttributeModifiersComponent.builder()
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                            new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                                    2.0f, EntityAttributeModifier.Operation.ADD_VALUE),
                            AttributeModifierSlot.MAINHAND)
                    .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                            new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                                    -2.f, EntityAttributeModifier.Operation.ADD_VALUE),
                            AttributeModifierSlot.MAINHAND)
                    .build())));

    public static final Item NAILED_BAT = registerItem("nailed_bat", new SwordItem(ToolMaterials.IRON, new Item.Settings().attributeModifiers(
            AttributeModifiersComponent.builder()
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                            new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                                    3.0f + ToolMaterials.IRON.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE),
                            AttributeModifierSlot.MAINHAND)
                    .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                            new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                                    -2.2f, EntityAttributeModifier.Operation.ADD_VALUE),
                            AttributeModifierSlot.MAINHAND)
                    .build())));


    private static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, Cubliminal.id(id), item);
    }

    public static void init() {
        FuelRegistry.INSTANCE.add(WOODEN_PLANK.asItem(), 100);
	}
}
