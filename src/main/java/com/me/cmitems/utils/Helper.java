package com.me.cmitems.utils;

import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import static com.me.cmitems.CmItems.mc;

public class Helper {
    public static void print(String s) {
        System.out.println("[CoresModule] " + s);
    }

    public static void printErr(String s) {
        System.err.println("[CoresModule] " + s);
    }

    @Nullable
    public static ServerWorld getServerWorld() {
        if (mc.getServer() == null || mc.player == null) return null;
        return mc.getServer().getWorld(mc.player.getWorld().getRegistryKey());
    }
}
