package com.me.cmitems.entities.bullet.pistol;

import com.me.cmitems.creator.ModEntity;
import com.me.cmitems.entities.bullet.Bullet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PistolBullet extends Bullet {
    public static final float bulletWidth = 0.375f;
    public static final EntityType<PistolBullet> PISTOL_BULLET = ModEntity.register(
            "pistol_bullet",
            PistolBullet::new,
            SpawnGroup.MISC,
            bulletWidth, // 8px
            0.125f // 2px
    );

    public PistolBullet(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
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
    public Bullet.Type getBulletType() {
        return Bullet.Type.PISTOL;
    }

}
