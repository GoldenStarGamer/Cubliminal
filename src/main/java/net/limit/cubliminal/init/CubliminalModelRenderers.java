package net.limit.cubliminal.init;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.client.render.SkyboxRenderer;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.minecraft.registry.Registry;

public class CubliminalModelRenderers {

	public static final SpecialModelRenderer MANILA_SKYBOX_RENDERER =
		get("manila_skybox", new SkyboxRenderer("manila"));

	public static <S extends SpecialModelRenderer> S get(String id, S modelRenderer) {
		return Registry.register(SpecialModelRenderer.SPECIAL_MODEL_RENDERER, Cubliminal.id(id), modelRenderer);
	}

	public static void init() {
	}
}
