package com.me.cmitems.items.guns.pistol;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.items.guns.Gun;
import net.minecraft.item.Item;

public class BasicPistol extends Gun {
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
        this.gunType = Gun.Type.PISTOL;
        this.bulletType = Bullet.Type.PISTOL;
        this.cooldownDuration = 5;
    }

    public static void register() {
        System.out.println("Registered Basic Pistol");
    }
}
