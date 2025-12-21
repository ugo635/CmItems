package com.me.cmitems.Entities;

import com.me.cmitems.Register;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.me.cmitems.CmItems.mc;

public class GravityArrow {
    public static List<HashMap<String, Object>> arrows = new ArrayList<>();

    public static void register() {
        Register.onTick(5, args -> arrowPull());
    }

    private static void arrowPull() {
        if (mc.world == null) return;

        for (int i = 0; i < arrows.size(); i++) {
            HashMap<String, Object> map = arrows.get(i);
            ArrowEntity arrow = (ArrowEntity) map.get("entity");
            if (arrow.isRemoved()) {
                arrows.remove(i);
                i--;
            }
        }

        Iterable<Entity> entities = mc.world.getEntities();

        for (Entity entity : entities) {

            if (entity instanceof LivingEntity livingEntity) {
                ArrowEntity arrow = getClosestArrow(livingEntity);
                if (arrow == null) {
                    continue;
                }

                double gravityPullValue = getGravityPull(arrow);

                double gravityPull = arrow.distanceTo(livingEntity) < gravityPullValue
                        ? gravityPullValue
                        : 0.0;

                System.out.println("Gravity Pull: " + gravityPull);

                Vec3d direction = new Vec3d(
                        arrow.getX() - livingEntity.getX(),
                        arrow.getY() - livingEntity.getY(),
                        arrow.getZ() - livingEntity.getZ()
                ).normalize();

                livingEntity.setVelocity(
                        direction
                );
            }
        }
    }

    private static double getGravityPull(ArrowEntity arrow) {
        String arrUUID = arrow.getUuidAsString();
        for (HashMap<String, Object> map : arrows) {
            if (map.get("uuid").equals(arrUUID)) {
                return (double) map.get("gravityPull");
            }
        }
        return 0.0;
    }

    private static ArrowEntity getClosestArrow(Entity entity) {
        if (arrows.isEmpty()) return null;

        ArrowEntity closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (HashMap<String, Object> map : arrows) {
            ArrowEntity arrow = (ArrowEntity) map.get("entity");
            double distance = entity.distanceTo(arrow);
            System.out.println("Distance to arrow: " + distance);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = arrow;
            }
        }

        return closest;
    }

}