package com.me.cmitems.items.guns.pistol.basic;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BasicPistolRenderer extends GeoItemRenderer<BasicPistolGun> {
    public BasicPistolRenderer() {
        super(new BasicPistolModel());
    }
}
