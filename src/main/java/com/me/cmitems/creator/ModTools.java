package com.me.cmitems.creator;

import com.me.cmitems.CustomCreativeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.me.cmitems.CmItems.MOD_ID;

// This is the same as ModItems, but made for tools, nothing really changes, just for clarity and organization.
// May be further modified to include more tool-specific features, such as durability, tool material, enchantability, etc.
// Or maybe even a parent class for specific tool types (pickaxes, axes, shovels, etc.)
public class ModTools {

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
}
