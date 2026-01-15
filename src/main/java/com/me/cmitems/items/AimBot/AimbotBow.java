package com.me.cmitems.items.AimBot;

import com.me.cmitems.ModItems;
import com.me.cmitems.items.GravityBow.GravityArrow;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.me.cmitems.CmItems.mc;

public class AimbotBow extends BowItem {
    public static Item AIMBOT_BOW = ModItems.register(
            "aimbot_bow",
            AimbotBow::new,
            new Settings()
                    .maxCount(1) // Sets the maximum stack count to 1
    );

    public static List<LivingEntity> entities;
    public static LivingEntity closest = null;
    public static float flt;

    public static void register() {
        System.out.println("Registered Aimbot Bow");
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.getItem() == AIMBOT_BOW) {
                list.add(Text.translatable("itemTooltip.cmitems.aimbot_bow_l1"));
            }
        });

        WorldRenderEvents.END.register(context -> {
            if (mc.player == null || mc.player.getMainHandStack().getItem() != AIMBOT_BOW) return;

            World world = mc.player.getWorld();

            Box box = new Box(
                    mc.player.getX() - 100, mc.player.getY() - 100, mc.player.getZ() -100,
                    mc.player.getX() + 100, mc.player.getY() + 100, mc.player.getZ() + 100
            );

            closest = null;

            entities = world.getEntitiesByType(
                    TypeFilter.instanceOf(LivingEntity.class),
                    box,
                    e -> e != mc.player && e.isAlive()
            );

            for (LivingEntity entity : entities) {
                //if (!(entity instanceof WitherSkeletonEntity)) continue;

                if (closest == null || mc.player.squaredDistanceTo(entity) < mc.player.squaredDistanceTo(closest)) {
                    closest = entity;
                }
            }


            if (closest == null) return;

            List<Float> yp = getYawPitchTo(mc.player, closest);

            mc.player.setYaw(yp.get(0));
            mc.player.setPitch(yp.get(1));

        });
    }

    public AimbotBow(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack item) {
        return true;
    }

    private static List<Float> getYawPitchTo(Entity from, Entity to) {
        double dx = to.getX() - from.getX();
        double dy = to.getEyeY() - from.getEyeY();
        double dz = to.getZ() - from.getZ();

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) ((float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distXZ));

        return List.of(yaw, pitch);
    }

    @Override
    protected void shootAll(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, @Nullable LivingEntity target) {
        float f = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0.0F);
        flt = f;
        float g = projectiles.size() == 1 ? 0.0F : 2.0F * f / (float) (projectiles.size() - 1);
        float h = (float) ((projectiles.size() - 1) % 2) * g / 2.0F;
        float i = 1.0F;

        for (int j = 0; j < projectiles.size(); ++j) {
            ItemStack itemStack = projectiles.get(j);
            if (!itemStack.isEmpty()) {
                float k = h + i * (float) ((j + 1) / 2) * g;
                i = -i;
                int finalJ = j;

                ArrowEntity arrow = createArrowEntity(world, shooter, stack, itemStack, critical);

                ProjectileEntity.spawn(arrow, world, itemStack, (projectile) -> this.shoot(shooter, projectile, finalJ, speed, divergence, k, target));

                stack.damage(this.getWeaponStackDamage(itemStack), shooter, LivingEntity.getSlotForHand(hand));
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
    }


    @Override
    protected ArrowEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ArrowEntity arrow = new ArrowEntity(world, shooter, projectileStack.copyWithCount(1), weaponStack);
        AimbotArrow.arrows.add(new AimbotArrow.ArrowData(arrow, closest, flt));

        if (critical) {
            arrow.setCritical(true);
        }

        return arrow;
    }

}