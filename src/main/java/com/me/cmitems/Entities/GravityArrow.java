package com.me.cmitems. Entities;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.me.cmitems.CmItems.mc;

public class GravityArrow {
    public static class ArrowData {
        public final ArrowEntity entity;
        public final double gravityPull;
        public final String uuid;

        public ArrowData(ArrowEntity entity, double gravityPull) {
            this.entity = entity;
            this.gravityPull = gravityPull;
            this.uuid = entity.getUuidAsString();
        }
    }

    public static List<ArrowData> arrows = new ArrayList<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(GravityArrow::arrowPull);
    }

    private static void arrowPull(ServerWorld world) {
        // Remove dead arrows
        arrows.removeIf(data -> data.entity.isRemoved());

        // Iterate through all entities in the server world
        for (Entity entity : world.iterateEntities()) {
            // Skip arrows & spawner
            if (mc.player != null) {
                if (entity.getName().getString().equals(mc.player.getName().getString())) continue;
            }
            if (entity instanceof ArrowEntity) continue;

            ArrowData arrowData = getClosestArrow(entity);
            if (arrowData == null) continue;

            double distance = arrowData.entity.distanceTo(entity);

            if (distance < arrowData.gravityPull && distance > 0.25) {
                Vec3d direction = new Vec3d(
                        arrowData.entity.getX() - entity.getX(),
                        arrowData.entity.getY() - entity.getY(),
                        arrowData.entity.getZ() - entity.getZ()
                ).normalize();

                double pullStrength = arrowData.gravityPull * 0.05 * (arrowData.gravityPull - distance);

                entity.addVelocity(
                        direction.x * pullStrength,
                        direction.y * pullStrength,
                        direction. z * pullStrength
                );

                entity.velocityModified = true;
            }
        }
    }

    private static ArrowData getClosestArrow(Entity entity) {
        if (arrows. isEmpty()) return null;

        ArrowData closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (ArrowData data : arrows) {
            double distance = entity.distanceTo(data.entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = data;
            }
        }

        return closest;
    }
}