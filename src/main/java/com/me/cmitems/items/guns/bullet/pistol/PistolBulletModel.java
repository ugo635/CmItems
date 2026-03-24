package com.me.cmitems.items.guns.bullet.pistol;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class PistolBulletModel extends EntityModel<PistolBulletStateRenderer> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Identifier.of("cmitems", "pistol_bullet"), "main");
    private final ModelPart bullet;

    public PistolBulletModel(ModelPart root) {
        super(root);
        this.bullet = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 16, 16);
    }

}