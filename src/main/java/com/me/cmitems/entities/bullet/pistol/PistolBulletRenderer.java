package com.me.cmitems.entities.bullet.pistol;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class PistolBulletRenderer extends EntityRenderer<PistolBullet, PistolBulletStateRenderer> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/misc/white.png");
    private final PistolBulletModel model;

    public static void register() {
        System.out.println("Registered Pistol Bullet");
        EntityRendererRegistry.register(PistolBullet.PISTOL_BULLET, PistolBulletRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PistolBulletModel.LAYER_LOCATION, PistolBulletModel::getTexturedModelData);
    }

    public PistolBulletRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new PistolBulletModel(context.getPart(PistolBulletModel.LAYER_LOCATION));
    }

    @Override
    public boolean shouldRender(PistolBullet entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(PistolBulletStateRenderer state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.translate(0.0f, 0.1f, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));

        VertexConsumer buffer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    @Override
    public void updateRenderState(PistolBullet entity, PistolBulletStateRenderer state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        // Pass rotation from Entity to RenderState
        state.yaw = entity.lerpYaw(tickProgress);
        state.pitch = entity.getLerpedPitch(tickProgress);
    }

    @Override
    public PistolBulletStateRenderer createRenderState() {
        return new PistolBulletStateRenderer();
    }

}