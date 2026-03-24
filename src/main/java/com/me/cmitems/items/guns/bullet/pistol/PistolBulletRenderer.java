package com.me.cmitems.items.guns.bullet.pistol;

import com.me.cmitems.blocks.tnts.arrowtnt.ArrowTntEntity;
import com.me.cmitems.blocks.tnts.arrowtnt.ArrowTntEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.RotationAxis;

public class PistolBulletRenderer extends EntityRenderer<PistolBullet, PistolBulletStateRenderer> {
    private static final Identifier TEXTURE = Identifier.of("cmitems", "textures/entity/bullet/pistol_bullet.png");
    private final PistolBulletModel model;

    public static void register() {
        System.out.println("Registered Pistol Bullet");
        EntityRendererRegistry.register(PistolBullet.PISTOL_BULLET, PistolBulletRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PistolBulletModel.LAYER_LOCATION, PistolBulletModel::getTexturedModelData);
    }

    public PistolBulletRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Initialize your 3D model
        this.model = new PistolBulletModel(context.getPart(PistolBulletModel.LAYER_LOCATION));
    }

    @Override
    public void render(PistolBulletStateRenderer state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));

        // Use getEntityCutout to ensure the texture and transparency render correctly
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(net.minecraft.client.render.RenderLayer.getEntityCutout(TEXTURE));

        // This calls the final render method in the Model class,
        // which in turn renders the 'root' ModelPart you defined.
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

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