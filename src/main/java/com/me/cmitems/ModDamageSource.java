package com.me.cmitems;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageSource {
    public static final RegistryKey<DamageType> BULLET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("cmitems", "bullet"));

    public static void register() {
            System.out.println("Registered Bullet Damage Source");
    }
}
