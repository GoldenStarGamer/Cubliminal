package net.limit.cubliminal.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface GameRendererAccessor {

	double cubliminal$callGetFov(Camera camera, float tickDelta, boolean changingFov);

	void cubliminal$callTiltViewWhenHurt(MatrixStack matrices, float tickDelta);

	void cubliminal$callBobView(MatrixStack matrices, float tickDelta);

}
