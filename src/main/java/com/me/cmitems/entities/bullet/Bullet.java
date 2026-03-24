package com.me.cmitems.entities.bullet;

import com.me.cmitems.entities.bullet.pistol.PistolBullet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class Bullet extends ProjectileEntity {
    protected LivingEntity shooter;
    protected World world;

    public Bullet(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
        this.world = world;
        this.shooter = null;
    }

    public void setShooter(LivingEntity e) {
        this.shooter = e;
    }

    public abstract Type getBulletType();


    @Override
    public void tick() {
        super.tick();

        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d start = this.getPos();
        Vec3d motion = this.getVelocity();
        Vec3d end = start.add(motion);

        // Raycast for blocks
        BlockHitResult blockHit = world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        ));
        if (blockHit.getType() != HitResult.Type.MISS) {
            this.discard();
            return;
        }

        // Raycast for entities
        EntityHitResult hit = ProjectileUtil.raycast(
                this,
                start,
                end,
                this.getBoundingBox().stretch(motion).expand(1.0),
                this::shouldHit,
                0.0
        );

        if (hit != null) {
            RegistryKey<DamageType> arrowKey = DamageTypes.ARROW;
            RegistryEntry<DamageType> arrowTypeEntry = serverWorld.getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                    .getOrThrow(arrowKey);

            DamageSource ds = new DamageSource(arrowTypeEntry, shooter);
            hit.getEntity().damage(serverWorld, ds, 5f);
            this.discard();
            return;
        }

        // Move bullet
        this.updatePosition(end.x, end.y, end.z);

        // Get the right orientation
        this.setPitch((float) -Math.toDegrees(-Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        this.setYaw((float) (Math.toDegrees(Math.atan2(motion.z, motion.x)) - 90f));

        // Gravity
        this.addVelocity(0, -0.01, 0);
    }

    protected boolean shouldHit(Entity e) {
        return !e.isSpectator() && e.canHit() && !(e instanceof PistolBullet) && e != shooter;
    }


    public enum Type {
        PISTOL;

        public Bullet getBullet(World world) {
            return switch (this) {
                case PISTOL -> new PistolBullet(world, 0, 0, 0);
            };
        }
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

}