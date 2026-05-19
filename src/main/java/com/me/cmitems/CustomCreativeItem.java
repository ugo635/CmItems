package com.me.cmitems;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CustomCreativeItem {
    public static List<Item> CreativeMenu = new ArrayList<>();

    public static final RegistryKey<ItemGroup> CM_ITEMS = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(CmItems.MOD_ID, "item_group"));

    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.DIAMOND))
            .displayName(Text.translatable("itemGroup.cmitems"))
            .build();

    public static void register() {
        Registry.register(
                Registries.ITEM_GROUP,
                CM_ITEMS,
                CUSTOM_ITEM_GROUP
        );

        ItemGroupEvents.modifyEntriesEvent(CM_ITEMS).register(itemGroup -> {
            System.out.printf("Added %s items to the Creative Menu%n", CreativeMenu.size());
            CreativeMenu.forEach(itemGroup::add);
        });

    }
}
