package com.me.cmitems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.me.cmitems.CmItems.MOD_ID;

public class ModSimpleEntity {
    public static <T extends Entity> EntityType<T> register(
            String name,
            EntityType.EntityFactory<T> factory,
            SpawnGroup group,
            float width,
            float height
    ) {
        // 1. You correctly created the key here
        RegistryKey<EntityType<?>> entityKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, name));

        EntityType<T> entityType = Registry.register(
                Registries.ENTITY_TYPE,
                entityKey,
                EntityType.Builder.create(factory, group)
                        .dimensions(width, height)
                        .build(entityKey)
        );

        return entityType;
    }
}
