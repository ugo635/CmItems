package com.me.cmitems.items.guns.pistol;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.items.guns.GeneralGun;
import net.minecraft.item.Item;

public class BasicPistol extends GeneralGun {
    public static final Item PISTOL_GUN = ModItems.register(
            "basic_pistol",
            BasicPistol::new,
            new Item.Settings()
                    .maxCount(1)
    );

    public BasicPistol(Settings settings) {
        super(settings);
        this.maxAmmo = 16;
        this.ammo = 16;
        this.damage = 5f;
        this.gunType = GunType.PISTOL;
        this.bulletType = BulletType.PISTOL;
    }

    public static void register() {
        System.out.println("Registered Basic Pistol");
    }
}
