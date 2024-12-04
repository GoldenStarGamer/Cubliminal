package net.limit.cubliminal;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.limit.cubliminal.init.CubliminalBlocks;
import net.limit.cubliminal.init.CubliminalItems;
import net.limit.cubliminal.init.CubliminalRegistrar;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CubliminalDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		//pack.addProvider(AdvancementsProvider::new);
	}

	/*
	static class AdvancementsProvider extends FabricAdvancementProvider {
		protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
			AdvancementEntry root = Advancement.Builder.create()
					.display(
							CubliminalBlocks.YELLOW_WALLPAPERS.asItem(),
							Text.translatable("advancements.backrooms.title"),
							Text.translatable("advancements.backrooms.description"),
							Cubliminal.id("textures/block/yellow_wallpapers.png"),
							AdvancementFrame.TASK, false, false, false)
					.criterion("entered_the_lobby", ChangedDimensionCriterion.Conditions.to(CubliminalRegistrar.THE_LOBBY_KEY))
					.build(consumer, Cubliminal.MOD_ID + "/root");

			AdvancementEntry enterLevelZero = Advancement.Builder.create().parent(root)
					.display(
							CubliminalItems.YELLOW_WALLPAPER,
							Text.translatable("advancements.backrooms.enter_the_lobby.title"),
							Text.translatable("advancements.backrooms.enter_the_lobby.description"),
							null,
							AdvancementFrame.TASK, true, true, false)
					.criterion("entered_the_lobby", ChangedDimensionCriterion.Conditions.to(CubliminalRegistrar.THE_LOBBY_KEY))
					.build(consumer, Cubliminal.MOD_ID + "/the_lobby");

			AdvancementEntry manilaRoom = Advancement.Builder.create().parent(enterLevelZero)
					.display(
							CubliminalItems.YELLOW_WALLPAPER,
							Text.translatable("advancements.backrooms.enter_manila_room.title"),
							Text.translatable("advancements.backrooms.enter_manila_room.description"),
							null,
							AdvancementFrame.TASK, true, true, false)
					.build(consumer, Cubliminal.MOD_ID + "/the_lobby");
		}
	}
	 */
}
