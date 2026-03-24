package com.me.cmitems.items.guns;

import com.me.cmitems.items.guns.bullet.Bullet;
import com.me.cmitems.items.guns.bullet.pistol.PistolBullet;
import com.me.cmitems.utils.Chat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class GeneralGun extends Item {
    protected double maxAmmo;
    protected int ammo;
    protected float damage;
    protected GunType gunType;
    protected BulletType bulletType;
    protected Entity shooter;

    public GeneralGun(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        Chat.chat("§aClicked");
        Bullet bullet = new PistolBullet(world, user.getX(), user.getEyeY(), user.getZ());
        world.spawnEntity(bullet);
        return super.use(world, user, hand);
    }

    protected void recoil() {

    }

    public enum GunType {
        PISTOL
    }

    public enum BulletType {
        PISTOL
    }
}
