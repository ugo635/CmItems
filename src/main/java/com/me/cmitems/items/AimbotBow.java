package com.me.cmitems.items;

import com.me.cmitems.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

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
                if (!(entity instanceof WitherSkeletonEntity)) continue;

                if (closest == null || mc.player.squaredDistanceTo(entity) < mc.player.squaredDistanceTo(closest)) {
                    closest = entity;
                }
            }


            if (closest == null) return;

            List<Float> yp = getYawPitchTo(mc.player, closest);
            System.out.println("Yaw: " + yp.get(0) + " Pitch: " + yp.get(1));

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

}