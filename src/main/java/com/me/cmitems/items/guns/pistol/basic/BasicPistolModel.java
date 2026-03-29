package com.me.cmitems.items.guns.pistol.basic;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BasicPistolModel extends GeoModel<BasicPistolGun> {

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.of("cmitems", "geckolib/models/item/guns/pistol/basic_pistol.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.of("cmitems", "textures/item/guns/pistol/basic/basic_pistol.png");
    }

    @Override
    public Identifier getAnimationResource(BasicPistolGun item) {
        return Identifier.of("cmitems", "geckolib/animation/item/guns/pistol/basic_pistol.animation.json");
    }
}