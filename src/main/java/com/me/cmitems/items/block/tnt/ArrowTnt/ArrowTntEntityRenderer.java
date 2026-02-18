package com.me.cmitems.items.block.tnt.ArrowTnt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ArrowTntEntityRenderer extends EntityRenderer<ArrowTntEntity, TntEntityRenderState> {
    private final BlockRenderManager blockRenderManager;

    public static void register() {
        EntityRendererRegistry.register(ArrowTntEntity.ARROW_TNT_TYPE, ArrowTntEntityRenderer::new);
    }

    public ArrowTntEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        float f = tntEntityRenderState.fuse;

        if (f < 10.0F) {
            float g = 1.0F - f / 10.0F;
            g = MathHelper.clamp(g, 0.0F, 1.0F);
            g *= g;
            g *= g;
            float h = 1.0F + g * 0.3F;
            matrixStack.scale(h, h, h);
        }

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        matrixStack.translate(-0.5F, -0.5F, 0.5F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        if (tntEntityRenderState.blockState != null) {
            TntMinecartEntityRenderer.renderFlashingBlock(
                    this.blockRenderManager,
                    tntEntityRenderState.blockState,
                    matrixStack,
                    vertexConsumerProvider,
                    i,
                    (int)f / 5 % 2 == 0
            );
        }

        matrixStack.pop();
        super.render(tntEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public TntEntityRenderState createRenderState() {
        return new TntEntityRenderState();
    }

    @Override
    public void updateRenderState(ArrowTntEntity arrowTntEntity, TntEntityRenderState tntEntityRenderState, float f) {
        super.updateRenderState(arrowTntEntity, tntEntityRenderState, f);
        tntEntityRenderState.fuse = (float)arrowTntEntity.getFuse() - f + 1.0F;
        tntEntityRenderState.blockState = arrowTntEntity.getBlockState();
    }
}