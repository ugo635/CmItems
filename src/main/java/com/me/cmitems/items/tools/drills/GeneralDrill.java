package com.me.cmitems.items.tools.drills;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.me.cmitems.CmItems.mc;

public abstract class GeneralDrill extends Item {
    protected static Direction blockFace;

    // Makes the 3x3 breaking work in creative mode, since postMine is not called in creative mode.
    static {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!player.isCreative()) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof GeneralDrill drill) {
                drill.mineArea(world, pos, player);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    public GeneralDrill(Settings settings) {
        super(settings);
    }

    public List<BlockPos> getBlockAreaPosition(BlockPos centerPos) {
        updateBlockFace();
        return blockFace == null
                ? List.of()
                : switch (blockFace) {
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

    protected void updateBlockFace() {
        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            blockFace = ((BlockHitResult) hit).getSide();
        }
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        if (toolComponent == null) {
            return false;
        } else {
            if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
                if (toolComponent.damagePerBlock() > 0) stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
                mineArea(world, pos, miner);
            }

            return true;
        }
    }

    protected void mineArea(World world, BlockPos centerPos, LivingEntity miner) {
        List<BlockPos> blocksToMine = new ArrayList<>(getBlockAreaPosition(centerPos));
        if (miner.isInCreativeMode()) blocksToMine.add(centerPos); // In creative mode, the center block is not mined by postMine, so we need to add it manually.

        for (BlockPos pos : blocksToMine) {
            BlockState state = world.getBlockState(pos);
            if (state.getHardness(world, pos) != 0.0f) {
                // Make the blocks drop if in survival mode, but not in creative mode.
                if (!miner.isInCreativeMode()) world.breakBlock(pos, true, miner);
                else world.breakBlock(pos, false);
            }
        }
    }

}
