package net.limit.cubliminal.entity.client;

import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.limit.cubliminal.init.CubliminalModelLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BacteriaRenderer extends MobEntityRenderer<BacteriaEntity, BacteriaModel<BacteriaEntity>> {
    private static final Identifier TEXTURE = Cubliminal.id("textures/entity/bacteria.png");
    public BacteriaRenderer(EntityRendererFactory.Context context) {
        super(context, new BacteriaModel<>(context.getPart(CubliminalModelLayers.BACTERIA)), 0.8f);
    }

    @Override
    public Identifier getTexture(BacteriaEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BacteriaEntity mobEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
