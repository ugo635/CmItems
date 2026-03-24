package com.me.cmitems.items.guns.bullet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class Bullet extends Entity {
    public Bullet(EntityType<?> type, World world) {
        super(type, world);
    }
}