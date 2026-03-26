package com.me.cmitems.items.guns.pistol;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.items.guns.Gun;
import net.minecraft.item.Item;

public class PistolGun extends com.me.cmitems.items.guns.Gun {
    public static final Item PISTOL_GUN = ModItems.register(
            "basic_pistol",
            PistolGun::new,
            new Item.Settings()
                    .maxCount(1)
    );

    public PistolGun(Settings settings) {
        super(settings);
        this.maxAmmo = 16;
        this.ammo = 16;
        this.damage = 5f;
        this.cooldown = 5;
        this.gunType = Gun.Type.PISTOL;
        this.bulletType = Bullet.Type.PISTOL;
        this.firemode = Gun.FireMode.SEMI;
    }

    public static void register() {
        System.out.println("Registered Basic Pistol");
    }
}
