package com.me.cmitems.items.AimBot;

import com.me.cmitems.items.GravityBow.GravityArrow;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AimbotArrow {

    public static class ArrowData {
        public final ArrowEntity entity;
        public final String uuid;
        public final LivingEntity target;
        public final float pullProgress;

        public ArrowData(ArrowEntity entity, LivingEntity aim, float pullProgress) {
            this.target = aim;
            this.entity = entity;
            this.uuid = entity.getUuidAsString();
            this.pullProgress = pullProgress;
        }
    }

    public static List<AimbotArrow.ArrowData> arrows = new ArrayList<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(AimbotArrow::aimbot);
    }

    private static void aimbot(ServerWorld world) {
        // Remove dead arrows
        arrows.removeIf(data ->
            data.entity.isRemoved() ||
            data.target.isRemoved() ||
            data.entity.isOnGround()
        );

        // Iterate through all arrows
        for (ArrowData arrow : arrows) {

            if (arrow == null) continue;

            Vec3d direction = new Vec3d(
                    arrow.target.getX() - arrow.entity.getX(),
                    arrow.target.getY() - arrow.entity.getY(),
                    arrow.target.getZ() - arrow.entity.getZ()
            ).normalize();


            arrow.entity.setVelocity(direction);

            arrow.entity.velocityModified = true;
        }
    }
}
