package com.me.cmitems.items.guns;

import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.utils.Chat;
import com.me.cmitems.utils.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Gun extends Item {
    protected double maxAmmo;
    protected int ammo;
    protected float damage;
    protected int cooldown;

    protected Gun.FireMode firemode;
    protected Gun.Type gunType;
    protected Bullet.Type bulletType;

    public Gun(Settings settings) {
        super(settings);
    }

    public void leftClick(PlayerEntity player, ItemStack stack) {
        if (player.getItemCooldownManager().isCoolingDown(stack)) return;

        this.shoot(Helper.getServerWorld(), player);
        this.recoil();
        this.setCooldown(stack, player);
    }

    //@Override
    //public ActionResult use(World world, PlayerEntity user, Hand hand) {}

    protected void setCooldown(ItemStack stack, PlayerEntity player) {
        player.getItemCooldownManager().set(stack, this.cooldown);
    }

    protected void shoot(World world, PlayerEntity user) {
        if (world == null) {
            Chat.chat("§cFailed To Shoot");
            return;
        }
        Bullet bullet = this.bulletType.getBullet(world);
        Vec3d look = user.getRotationVec(1.0f);

        Vec3d direction = look.multiply(5);

        bullet.setPosition(user.getEyePos().add(look.multiply(0.5)));
        bullet.setVelocity(direction);
        bullet.setPitch((float) -Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))));
        bullet.setYaw((float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90f));
        bullet.setShooter(user);

        world.spawnEntity(bullet);
        Chat.chat("§6Shot");
    }

    protected void recoil() {}


    public enum Type {
        PISTOL
    }

    public enum FireMode {
        SEMI,
        BURST,
        AUTO
    }

}
