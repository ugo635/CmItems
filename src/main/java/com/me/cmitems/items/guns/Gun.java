package com.me.cmitems.items.guns;

import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.utils.Chat;
import com.me.cmitems.utils.Helper;
import com.me.cmitems.utils.Register;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * <h3 style="color: white">Shooting handler: </h3>
 * @see com.me.cmitems.mixin.MouseMixin
 */
public abstract class Gun extends Item {
    protected double maxAmmo;
    protected int ammo;
    protected float damage;
    protected int cooldown;

    protected Gun.FireMode fireMode;
    protected Gun.Type gunType;
    protected Bullet.Type bulletType;

    public static boolean hasBeenReleased = true;

    public Gun(Settings settings) {
        super(settings);
    }

    static {
        Register.onClientTick(1, mc -> {
            if (mc.player == null) return;

            ItemStack stack = mc.player.getMainHandStack();
            if (!(stack.getItem() instanceof Gun gun)) return;

            if (gun.getFireMode() == Gun.FireMode.AUTO && !hasBeenReleased) {
                gun.leftClick(mc.player, stack);
            }
        });
    }

    public void leftClick(PlayerEntity player, ItemStack stack) {
        if (player.getItemCooldownManager().isCoolingDown(stack)) return;

        this.shoot(player);
        this.recoil();
        this.setCooldown(stack, player);
    }

    //@Override
    //public ActionResult use(World world, PlayerEntity user, Hand hand) {}

    protected void setCooldown(ItemStack stack, PlayerEntity player) {
        player.getItemCooldownManager().set(stack, this.cooldown);
    }

    protected void shoot(PlayerEntity shooter) {
        World world = Helper.getServerWorld();

        if (world == null) {
            Chat.chat("§cFailed To Shoot");
            return;
        }

        Bullet bullet = this.bulletType.getBullet(world);
        Vec3d look = shooter.getRotationVec(1.0f);

        Vec3d direction = look.multiply(5);

        bullet.setPosition(shooter.getEyePos().add(look.multiply(0.5)));
        bullet.setVelocity(direction);
        bullet.setPitch((float) -Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))));
        bullet.setYaw((float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90f));
        bullet.setShooter(shooter);

        world.spawnEntity(bullet);
        Chat.chat("§6Shot");
    }

    protected void recoil() {}

    public FireMode getFireMode() {
        return fireMode;
    }

    public enum Type {
        PISTOL
    }

    public enum FireMode {
        SEMI,
        BURST,
        AUTO
    }

}
