package com.me.cmitems.items.guns;

import com.me.cmitems.entities.bullet.Bullet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Gun extends Item {
    protected double maxAmmo;
    protected int ammo;
    protected float damage;

    protected Gun.Type gunType;
    protected Bullet.Type bulletType;

    public Gun(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) shoot(world, user);

        return ActionResult.SUCCESS;
    }

    protected void shoot(World world, PlayerEntity user) {
        if (user.getItemCooldownManager().isCoolingDown(user.getMainHandStack())) return;

        Bullet bullet = this.bulletType.getBullet(world);
        Vec3d look = user.getRotationVec(1.0f);

        Vec3d direction = look.multiply(5);

        bullet.setPosition(user.getEyePos().add(look.multiply(0.5)));
        bullet.setVelocity(direction);
        bullet.setPitch((float) -Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))));
        bullet.setYaw((float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90f));
        bullet.setShooter(user);

        world.spawnEntity(bullet);

        this.recoil();
    }

    protected void recoil() {

    }


    public enum Type {
        PISTOL
    }

}
