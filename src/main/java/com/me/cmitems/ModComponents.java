package com.me.cmitems;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<Double> GRAVITY_PULL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CmItems.MOD_ID, "gravity-pull"),
            ComponentType.<Double>builder()
                    .codec(Codec.DOUBLE) // For saving to disk
                    .packetCodec(PacketCodecs.DOUBLE) // For network synchronization
                    .build()
    );

    protected static void register() {
        //noinspection RedundantStringFormatCall // Remove warning
        System.out.println("Registering %s components".formatted(CmItems.MOD_ID));
    }
}