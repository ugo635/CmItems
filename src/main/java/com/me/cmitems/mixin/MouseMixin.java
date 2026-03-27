package com.me.cmitems.mixin;

import com.me.cmitems.items.guns.Gun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
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
    private void onLeftClick(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            if (client.player != null && client.currentScreen == null && client.player.getMainHandStack().getItem() instanceof Gun gun)
                gun.leftClick(client.player, client.player.getMainHandStack());
        }
    }
}