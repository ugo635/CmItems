package com.me.cmitems.items;

import com.me.cmitems.ModItems;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class GravityBow extends BowItem {
    public static Item GRAVITY_BOW = ModItems.register(
            "gravity_bow",
            GravityBow::new,
            new Item.Settings().maxCount(1)
    );

    public static void register() {
        System.out.println("Registered Gravity Bow");
    }

    public GravityBow(Settings settings) {
        super(settings);
        //appendTooltip(GRAVITY_BOW, mc.world, List.of(), null);
    }

    @Override
    public boolean hasGlint(ItemStack item) {
        return true;
    }

    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, Object context) {
        tooltip.add(Text.translatable("itemTooltip.cmitems.lightning_stick"));
    }

    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.getItem() == Items.ARROW;
    }
}
