package com.me.cmitems.items.guns.bullet.pistol;

import com.me.cmitems.creator.ModEntity;
import com.me.cmitems.items.guns.bullet.Bullet;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import static com.me.cmitems.CmItems.mc;

public class PistolBullet extends Bullet {
    public static final float bulletWidth = 0.375f;
    public static final EntityType<PistolBullet> PISTOL_BULLET = ModEntity.register(
            "pistol_bullet",
            PistolBullet::new,
            SpawnGroup.MISC,
            bulletWidth, // 8px
            0.125f // 2px
    );

    public final World world;
    public final Entity shooter;


    public PistolBullet(EntityType<?> type, World world) {
        super(type, world);
        this.world = world;
        this.shooter = null;
    }

    public PistolBullet(World world) {
        this(PISTOL_BULLET, world);
    }

    public PistolBullet(World world, Vec3d pos) {
        this(PISTOL_BULLET, world);
        this.setPosition(pos);
    }

    public PistolBullet(World world, double x, double y, double z) {
        this(PISTOL_BULLET, world);
        this.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (world instanceof ServerWorld serverWorld) {
            Vec3d start = this.getPos();
            Vec3d motion = this.getVelocity().normalize().multiply(speed);
            Vec3d end = start.add(motion);

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

                DamageSource ds = new DamageSource(arrowTypeEntry, mc.player); // TODO: Use shooter

                hit.getEntity().damage(serverWorld, ds, 5f);
                this.discard();
                return;
            }

            this.updatePosition(end.x, end.y, end.z);
        }
    }

    private boolean shouldHit(Entity e) {
        return !e.isSpectator() && e.canHit() && !(e instanceof PistolBullet) && e != shooter;
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
