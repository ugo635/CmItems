package com.me.cmitems.items;

import com.me.cmitems.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;


public class TntBow extends BowItem {
    public static Item TNT_BOW = ModItems.register(
            "tnt_bow",
            TntBow::new,
            new Item.Settings().maxCount(1)
    );


    public static void register() {
        System.out.println("Registered Tnt Bow");
    }

    public TntBow(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack item) {
        return true;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        } else {
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (itemStack.isEmpty()) {
                return false;
            } else {
                int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (f < 0.1) {
                    return false;
                } else {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld serverWorld && !list.isEmpty()) {
                        this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
                    }

                    world.playSound(
                            null,
                            playerEntity.getX(),
                            playerEntity.getY(),
                            playerEntity.getZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT,
                            SoundCategory.PLAYERS,
                            1.0F,
                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
                    );
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    return true;
                }
            }
        }
    }

    @Override
    protected void shootAll(
            ServerWorld world,
            LivingEntity shooter,
            Hand hand,
            ItemStack stack,
            List<ItemStack> projectiles,
            float speed,
            float divergence,
            boolean critical,
            @Nullable LivingEntity target
    ) {
        if (!(shooter instanceof PlayerEntity player)) return;

        Vec3d look = player.getRotationVector().normalize();
        Vec3d spawnPos = player.getEyePos().add(look.multiply(0.6));

        TntEntity tnt = new TntEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, player);
        world.spawnEntity(tnt);

        tnt.setVelocity(look.multiply(speed * 2));

        stack.damage(1, player, LivingEntity.getSlotForHand(hand));
    }


    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.getItem() == Items.TNT;
    }

}