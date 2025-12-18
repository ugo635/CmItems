package com.me.cmitems.items;

import com.me.cmitems.ModItems;
import net.minecraft.world.item.Item;

public class EnderBow {
    public static Item ENDER_BOW = ModItems.register("ender_bow", Item::new, new Item.Properties());
    public static void register() {
        System.out.println("Registered Ender Bow");
    }
}