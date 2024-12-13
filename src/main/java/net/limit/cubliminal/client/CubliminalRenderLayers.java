package net.limit.cubliminal.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.Cubliminal;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

@Environment(EnvType.CLIENT)
public class CubliminalRenderLayers {
    public static final ShaderProgramKey RENDERTYPE_CUBLIMINAL_MANILA_SKYBOX = ShaderProgramKeys.register("rendertype_cubliminal_manila_skybox", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

    public static final RenderPhase.ShaderProgram MANILA_PROGRAM = new RenderPhase.ShaderProgram(RENDERTYPE_CUBLIMINAL_MANILA_SKYBOX);

    public static final RenderLayer MANILA = RenderLayer.of("manila", VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS, 1536, false, false,
            RenderLayer.MultiPhaseParameters.builder().program(MANILA_PROGRAM).texture(
                    RenderPhase.Textures.create()
                            .add(Cubliminal.id("textures/sky/manila_" + 0 + ".png"), false, false)
                            .add(Cubliminal.id("textures/sky/manila_" + 1 + ".png"), false, false)
                            .add(Cubliminal.id("textures/sky/manila_" + 2 + ".png"), false, false)
                            .add(Cubliminal.id("textures/sky/manila_" + 3 + ".png"), false, false)
                            .add(Cubliminal.id("textures/sky/manila_" + 4 + ".png"), false, false)
                            .add(Cubliminal.id("textures/sky/manila_" + 5 + ".png"), false, false)
                            .build()).build(false));


    public static final ShaderProgramKey RENDERTYPE_BLOOM_DOT = ShaderProgramKeys.register("rendertype_bloom_dot", VertexFormats.POSITION_COLOR);

    public static final RenderPhase.ShaderProgram BLOOM_PROGRAM = new RenderPhase.ShaderProgram(RENDERTYPE_BLOOM_DOT);

    public static final RenderLayer BLOOM = RenderLayer.of("bloom", VertexFormats.POSITION_COLOR_LIGHT,
            VertexFormat.DrawMode.QUADS, 1536, false, false,
            RenderLayer.MultiPhaseParameters.builder().program(BLOOM_PROGRAM).build(false));


    public static void init() {
    }
}
