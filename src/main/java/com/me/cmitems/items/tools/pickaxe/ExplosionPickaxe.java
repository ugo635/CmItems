package com.me.cmitems.items.tools.pickaxe;

import com.me.cmitems.creator.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplosionPickaxe extends Item {
    public static final Item EXPLOSION_PICKAXE =  ModItems.register(
        "explosion_pickaxe",
            ExplosionPickaxe::new,
            new Item.Settings()
                    .pickaxe(ToolMaterial.DIAMOND, 1f, -2.8f)
                    .maxCount(1)
    );

    public ExplosionPickaxe(Settings settings) {
        super(settings);
    }

    public static void register() {
        System.out.println("Registered Explosion Pickaxe");
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        TntEntity tnt = new TntEntity(world,  pos.getX(), pos.getY(), pos.getZ(), miner);
        tnt.setFuse(0);
        world.spawnEntity(tnt);
        return super.postMine(stack, world, state, pos, miner);
    }
}
