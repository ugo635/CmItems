package com.me.cmitems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

import static com.me.cmitems.CmItems.MOD_ID;

public class ModItems {
    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        // Add to the creative tab.
        CustomCreativeItem.CreativeMenu.add(item);

        return item;
    }

    public static void appendTooltipLightningStick(ItemStack stack, Item.TooltipContext context, List<Text> tooltip) {
        tooltip.add(Text.translatable("itemTooltip.cmitems.lightning_stick"));
    }

}