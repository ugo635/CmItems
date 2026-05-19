package com.me.cmitems.toolmaterial;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class DrillMaterial {
    public static final ToolMaterial DIAMOND_DRILL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            (int) (ToolMaterial.DIAMOND.durability() * 1.5f),
            ToolMaterial.DIAMOND.speed(),
            ToolMaterial.DIAMOND.attackDamageBonus(),
            ToolMaterial.DIAMOND.enchantmentValue(),
            ToolMaterial.DIAMOND.repairItems()
    );
}
