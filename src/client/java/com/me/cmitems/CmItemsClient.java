package com.me.cmitems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import com.me.cmitems.items.LightningStick;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class CmItemsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((ItemStack stack, Item.TooltipContext context, TooltipType tooltipType, List<Text> lines) -> {
            if (stack.getItem() == LightningStick.LIGHTNING_STICK) {
                ModItems.appendTooltipLightningStick(stack, context, lines);
            }
        });
    }
}
