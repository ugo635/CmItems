package com.me.cmitems.utils;

import com.me.cmitems.items.guns.Gun;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

public class GunHelper {

    private static final String COOLDOWN_TAG = "gun_cooldown";

    static {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                for (ItemStack stack : player.getInventory()) {
                    if (stack.getItem() instanceof Gun) {
                        GunHelper.tickCooldown(stack);
                    }
                }
            }
        });
    }

    public static int getCooldown(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 0;
        NbtCompound nbt = comp.copyNbt();
        return nbt.contains(COOLDOWN_TAG) ? nbt.getInt(COOLDOWN_TAG).orElse(0) : 0;
    }

    public static void setCooldown(ItemStack stack, int ticks) {
        NbtCompound nbt = new NbtCompound();
        NbtComponent existing = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (existing != null) nbt = existing.copyNbt();

        nbt.putInt(COOLDOWN_TAG, ticks);

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static void tickCooldown(ItemStack stack) {
        int cd = getCooldown(stack);
        if (cd > 0) setCooldown(stack, cd - 1);
    }

    public static boolean onCooldown(ItemStack stack) {
        return getCooldown(stack) > 0;
    }
}