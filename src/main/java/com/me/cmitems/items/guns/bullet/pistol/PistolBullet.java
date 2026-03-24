package com.me.cmitems.items.guns.bullet.pistol;

import com.me.cmitems.creator.ModEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class PistolBullet extends Entity {
    public static final EntityType<PistolBullet> PISTOL_BULLET = ModEntity.register(
            "pistol_bullet",
            PistolBullet::new,
            SpawnGroup.MISC,
            0.5f, // 8px
            0.125f // 2px
    );


    public PistolBullet(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        // Basic gravity and movement logic
        // this.setVelocity(this.getVelocity().add(0, -0.04, 0)); // Gravity - Purposely commented, don't change it!
        this.move(net.minecraft.entity.MovementType.SELF, this.getVelocity());

        // Update rotation based on velocity
        if (this.getVelocity().lengthSquared() > 0) {
            this.setYaw((float) (Math.atan2(this.getVelocity().x, this.getVelocity().z) * (180 / Math.PI)));
            this.setPitch((float) (Math.atan2(this.getVelocity().y, this.getVelocity().horizontalLength()) * (180 / Math.PI)));
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
