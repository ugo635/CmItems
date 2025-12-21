package com.me.cmitems.items;

import com.me.cmitems.ModComponents;
import com.me.cmitems.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class GravityBow extends BowItem {
    public static Item GRAVITY_BOW = ModItems.register(
            "gravity_bow",
            GravityBow::new,
            new Item.Settings()
                    .component(ModComponents.GRAVITY_PULL, 5.0) // Adds a gravity pull component with a value of 5.0
                    .maxCount(1) // Sets the maximum stack count to 1
    );

    public static void register() {
        System.out.println("Registered Gravity Bow");
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.getItem() == GRAVITY_BOW) {
                list.add(Text.translatable("itemTooltip.cmitems.gravity_bow_l1"));
                list.add(Text.translatable("itemTooltip.cmitems.gravity_bow_l2").append(Text.of("§a" + stack.get(ModComponents.GRAVITY_PULL))));
            }
        });
    }

    public GravityBow(Settings settings) {
        super(settings);
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
                if ((double) f < 0.1) {
                    return false;
                } else {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld) world;
                        if (!list.isEmpty()) {
                            this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, (LivingEntity) null);
                        }
                    }

                    world.playSound((Entity) null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    return true;
                }
            }
        }
    }

    @Override
    protected void shootAll(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, @Nullable LivingEntity target) {
        float f = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0.0F);
        float g = projectiles.size() == 1 ? 0.0F : 2.0F * f / (float) (projectiles.size() - 1);
        float h = (float) ((projectiles.size() - 1) % 2) * g / 2.0F;
        float i = 1.0F;

        for (int j = 0; j < projectiles.size(); ++j) {
            ItemStack itemStack = projectiles.get(j);
            if (!itemStack.isEmpty()) {
                float k = h + i * (float) ((j + 1) / 2) * g;
                i = -i;
                int finalJ = j;

                itemStack.set(ModComponents.GRAVITY_PULL, getGravityPull(stack));
                ProjectileEntity arrow = this.createArrowEntity(world, shooter, stack, itemStack, critical);
                arrow.onLanding();

                ProjectileEntity.spawn(arrow, world, itemStack, (projectile) -> this.shoot(shooter, projectile, finalJ, speed, divergence, k, target));
                stack.damage(this.getWeaponStackDamage(itemStack), shooter, LivingEntity.getSlotForHand(hand));
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

    }

    private Double getGravityPull(ItemStack stack) {
        return stack.get(ModComponents.GRAVITY_PULL);
    }


    @Override
    public boolean hasGlint(ItemStack item) {
        return true;
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
        return stack -> stack.getItem() == Items.ARROW;
    }
}
