package net.limit.cubliminal.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.block.entity.USBlockBlockEntity;
import net.limit.cubliminal.event.noclip.NoclipEngine;
import net.limit.cubliminal.event.sanity.SanityManager;

public interface PEAccessor {

	NoclipEngine getNoclipEngine();

	SanityManager getSanityManager();

	@Environment(EnvType.CLIENT)
	void openUSBlockScreen(USBlockBlockEntity blockEntity);
}
