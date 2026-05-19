package com.me.cmitems.items.guns.pistol.basic;

import com.google.common.base.Suppliers;
import com.me.cmitems.creator.ModItems;
import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.items.guns.Gun;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BasicPistolGun extends Gun {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final Item PISTOL_GUN = ModItems.register(
            "guns/basic_pistol",
            BasicPistolGun::new,
            new Item.Settings()
                    .maxCount(1)
    );

    public BasicPistolGun(Settings settings) {
        super(settings);
        this.maxAmmo = 16;
        this.ammo = 16;
        this.damage = 5f;
        this.cooldown = 5;
        this.gunType = Gun.Type.PISTOL;
        this.bulletType = Bullet.Type.PISTOL;
        this.fireMode = Gun.FireMode.SEMI;
    }

    public static void register() {
        System.out.println("Registered Basic Pistol");
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<BasicPistolRenderer> renderer = Suppliers.memoize(BasicPistolRenderer::new);

            @Override
            public @Nullable GeoItemRenderer<BasicPistolGun> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

}
