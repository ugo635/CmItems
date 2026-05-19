package com.me.cmitems.items.tools.drills;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.toolmaterial.DrillMaterial;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

// FIXME: Enchantments can't be applied through enchantment table, but can be applied through anvil.
public class DiamondDrill extends GeneralDrill {
    public static Item DIAMOND_DRILL = ModItems.register(
            "tools/diamond_drill",
            DiamondDrill::new,
            new Item.Settings()
                    .pickaxe(DrillMaterial.DIAMOND_DRILL_MATERIAL, 1.0f, -2.8f)
                    .maxCount(1)
    );

    public DiamondDrill(Settings settings) {
        super(settings);
    }

    public static void register() {
        System.out.println("Registered Diamond Drill");
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.getItem() == DIAMOND_DRILL) {
                list.add(1, Text.translatable("itemTooltip.cmitems.diamond_drill"));
            }
        });

    }

}
