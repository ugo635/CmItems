package com.me.cmitems.mixin;

import com.me.cmitems.items.guns.Gun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        if (client.player == null || client.currentScreen != null) return;

        ItemStack stack = client.player.getMainHandStack();

        if (action == GLFW.GLFW_RELEASE) {
            Gun.hasBeenReleased = true;
        }

        if (!(stack.getItem() instanceof Gun gun)) return;

        if (action == GLFW.GLFW_PRESS) {
            gun.leftClick(client.player, stack);
            Gun.hasBeenReleased = false;
        }

    }

}