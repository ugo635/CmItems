package com.me.cmitems.items.guns.bullet.pistol;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PistolBulletModel extends EntityModel<PistolBulletStateRenderer> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Identifier.of("cmitems", "pistol_bullet"), "main");
    private final ModelPart bullet;

    public PistolBulletModel(ModelPart root) {
        super(root.getChild("main"));
        this.bullet = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // This is where you define "Length" (the 4.0f here)
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-0.5f, 0.0f, -2.0f, 1.0f, 1.0f, 4.0f), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(PistolBulletStateRenderer state) {
        // Angles are handled by the renderer matrices in 1.21.4
    }
}