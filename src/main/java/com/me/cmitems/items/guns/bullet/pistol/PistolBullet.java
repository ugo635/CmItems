package com.me.cmitems.items.guns.bullet.pistol;

import com.me.cmitems.creator.ModEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.me.cmitems.CmItems.mc;

public class PistolBullet extends Entity {
    public static final float bulletWidth = 0.375f;
    public static final EntityType<PistolBullet> PISTOL_BULLET = ModEntity.register(
            "pistol_bullet",
            PistolBullet::new,
            SpawnGroup.MISC,
            bulletWidth, // 8px
            0.125f // 2px
    );

    public final World world;


    public PistolBullet(EntityType<?> type, World world) {
        super(type, world);
        this.world = world;
    }

    public PistolBullet(World world) {
        this(PISTOL_BULLET, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (world instanceof ServerWorld serverWorld) {
            // compute next position
            Vec3d start = this.getPos();
            Vec3d motion = this.getVelocity().normalize().multiply(speed);
            Vec3d end = start.add(motion);

            // raycast to detect entities
            EntityHitResult hit = ProjectileUtil.raycast(this, start, end,
                    this.getBoundingBox().stretch(motion),
                    e -> e != this && !e.isSpectator(),
                    0.375
            );

            if (hit != null) {
                // 1. Get the RegistryKey for the specific damage type
                RegistryKey<DamageType> arrowKey = DamageTypes.ARROW;

                RegistryEntry<DamageType> arrowTypeEntry = serverWorld.getRegistryManager()
                        .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                        .getOrThrow(arrowKey);

                DamageSource ds = new DamageSource(arrowTypeEntry, mc.player);

                hit.getEntity().damage(serverWorld, ds, 5f);
                this.discard(); // remove bullet on hit
                return;
            }

            // move bullet
            this.updatePosition(end.x, end.y, end.z);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
