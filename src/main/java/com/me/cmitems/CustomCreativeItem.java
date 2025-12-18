package com.me.cmitems;

import com.me.cmitems.items.EnderBow;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.List;

public class CustomCreativeItem {
    public static List<Item> CreativeMenu = new ArrayList<>();
    public static final ResourceKey<CreativeModeTab> CM_ITEMS = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(CmItems.MOD_ID, "item_group"));
    public static final CreativeModeTab CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.DIAMOND))
            .title(Component.translatable("itemGroup.cmitems"))
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CM_ITEMS, CUSTOM_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(CM_ITEMS).register(itemGroup -> {
            for (Item item : CreativeMenu) {
                itemGroup.accept(item);
            }
        });
    }

}