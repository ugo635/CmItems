package com.me.cmitems.items.guns.pistol;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.items.guns.Gun;
import net.minecraft.item.Item;

public class AutoPistolGun extends Gun {
    public static final Item AUTO_PISTOL_GUN = ModItems.register(
            "guns/auto_pistol",
            AutoPistolGun::new,
            new Item.Settings()
                    .maxCount(1)
    );

    public AutoPistolGun(Item.Settings settings) {
        super(settings);
        this.maxAmmo = 16;
        this.ammo = 16;
        this.damage = 5f;
        this.cooldown = 2;
        this.gunType = Gun.Type.PISTOL;
        this.bulletType = Bullet.Type.PISTOL;
        this.fireMode = Gun.FireMode.AUTO;
    }

    public static void register() {
        System.out.println("Registered Auto Pistol");
    }
}
