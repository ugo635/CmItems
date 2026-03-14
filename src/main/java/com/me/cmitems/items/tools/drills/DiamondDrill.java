package com.me.cmitems.items.tools.drills;

import com.me.cmitems.creator.ModItems;
import com.me.cmitems.toolmaterial.DrillMaterial;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

// FIXME: Enchantments can't be applied through enchantment table, but can be applied through anvil.
public class DiamondDrill extends Item {
    public static Item DIAMOND_DRILL = ModItems.register(
            "diamond_drill",
            DiamondDrill::new,
            new Item.Settings()
                    .pickaxe(DrillMaterial.DIAMOND_DRILL_MATERIAL, 1.0f, -2.8f)
                    .maxCount(1)
    );

    public static Direction blockFace;

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

        // When start mining, set the direction to the face of the block being mined. This is used to determine the 3x3 area to mine.
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            blockFace = direction;
            return ActionResult.PASS;
        });
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        if (toolComponent == null) {
            return false;
        } else {
            if (!world.isClient && state.getHardness(world, pos) != 0.0f && toolComponent.damagePerBlock() > 0) {
                stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
                mine3x3(stack, world, pos, miner);
            }

            return true;
        }
    }

    private void mine3x3(ItemStack stack, World world, BlockPos centerPos, LivingEntity miner) {
        List<BlockPos> blocksToMine = get3x3BlockPositions(centerPos);

        for (BlockPos pos : blocksToMine) {
            BlockState state = world.getBlockState(pos);
            if (state.getHardness(world, pos) != 0.0f) {
                world.breakBlock(pos, true, miner);
            }
        }
    }

    private List<BlockPos> get3x3BlockPositions(BlockPos centerPos) {
        return switch (blockFace) {
            case UP, DOWN -> List.of(
                    centerPos.north().west(),
                    centerPos.north(),
                    centerPos.north().east(),
                    centerPos.west(),
                    centerPos.east(),
                    centerPos.south().west(),
                    centerPos.south(),
                    centerPos.south().east()
            );

            case NORTH, SOUTH -> List.of(
                    centerPos.up().west(),
                    centerPos.up(),
                    centerPos.up().east(),
                    centerPos.west(),
                    centerPos.east(),
                    centerPos.down().west(),
                    centerPos.down(),
                    centerPos.down().east()
            );

            case EAST, WEST -> List.of(
                    centerPos.up().north(),
                    centerPos.up(),
                    centerPos.up().south(),
                    centerPos.north(),
                    centerPos.south(),
                    centerPos.down().north(),
                    centerPos.down(),
                    centerPos.down().south()
            );

        };

    }

}
