package net.limit.cubliminal.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.mixin.client.GameRendererAccessor;
import net.ludocrypt.specialmodels.api.SpecialModelRenderer;
import net.ludocrypt.specialmodels.impl.render.MutableQuad;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;

public class SkyboxRenderer extends SpecialModelRenderer {

	private final String id;

	public SkyboxRenderer(String id) {
		this.id = id;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void setup(MatrixStack matrices, Matrix4f viewMatrix, Matrix4f positionMatrix, float tickDelta,
			ShaderProgram shader, BlockPos origin) {

		for (int i = 0; i < 6; i++) {
			RenderSystem.setShaderTexture(i, Cubliminal.id("textures/sky/" + id + "_" + i + ".png"));
		}

		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		Matrix4f matrix = new MatrixStack().peek().getPositionMatrix();
		matrix.rotate(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrix.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

		if (shader.getUniform("RotMat") != null) {
			shader.getUniform("RotMat").set(matrix);
		}

		MatrixStack matrixStack = new MatrixStack();
		((GameRendererAccessor) client.gameRenderer).callTiltViewWhenHurt(matrixStack, tickDelta);

		if (client.options.getBobView().getValue()) {
			((GameRendererAccessor) client.gameRenderer).callBobView(matrixStack, tickDelta);
		}

		if (shader.getUniform("bobMat") != null) {
			shader.getUniform("bobMat").set(matrixStack.peek().getPositionMatrix());
		}

	}

	@Override
	@Environment(EnvType.CLIENT)
	public MutableQuad modifyQuad(ChunkRendererRegion chunkRenderRegion, BlockPos pos, BlockState state, BakedModel model,
			BakedQuad quadIn, long modelSeed, MutableQuad quad) {
		quad.getV1().setUv(new Vec2f(0.0F, 0.0F));
		quad.getV2().setUv(new Vec2f(0.0F, 1.0F));
		quad.getV3().setUv(new Vec2f(1.0F, 1.0F));
		quad.getV4().setUv(new Vec2f(1.0F, 0.0F));
		return quad;
	}

}
