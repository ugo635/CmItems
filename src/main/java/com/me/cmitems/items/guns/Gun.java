package com.me.cmitems.items.guns;

import com.me.cmitems.CmItems;
import com.me.cmitems.entities.bullet.Bullet;
import com.me.cmitems.utils.Chat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.me.cmitems.CmItems.mc;

public abstract class Gun extends Item {
    protected double maxAmmo;
    protected int ammo;
    protected float damage;
    protected int cooldown;

    protected Gun.Type gunType;
    protected Bullet.Type bulletType;

    public Gun(Settings settings) {
        super(settings);
    }

    static {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            PlayerEntity player = CmItems.mc.player;
            if (player == null) return;

            // Attack Key = Left Click
            KeyBinding attackKey = mc.options.attackKey;
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof Gun gun) {
                // Left click
                if (attackKey.isPressed()) {
                    gun.leftClick(world, player, stack);
                }
            }

        });
    }

    protected void leftClick(World world, PlayerEntity player, ItemStack stack) {
        if (player.getItemCooldownManager().isCoolingDown(stack)) return;

        this.shoot(world, player);
        this.recoil();
        this.setCooldown(stack, player);
        Chat.chat("§6Shot");
    }

    //@Override
    //public ActionResult use(World world, PlayerEntity user, Hand hand) {}

    protected void setCooldown(ItemStack stack, PlayerEntity player) {
        player.getItemCooldownManager().set(stack, this.cooldown);
    }

    protected void shoot(World world, PlayerEntity user) {
        Bullet bullet = this.bulletType.getBullet(world);
        Vec3d look = user.getRotationVec(1.0f);

        Vec3d direction = look.multiply(5);

        bullet.setPosition(user.getEyePos().add(look.multiply(0.5)));
        bullet.setVelocity(direction);
        bullet.setPitch((float) -Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))));
        bullet.setYaw((float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90f));
        bullet.setShooter(user);

        world.spawnEntity(bullet);

    }

    protected void recoil() {}


    public enum Type {
        PISTOL
    }

}
