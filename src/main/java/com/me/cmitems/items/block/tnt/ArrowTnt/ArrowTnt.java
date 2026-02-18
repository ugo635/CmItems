package com.me.cmitems.items.block.tnt.ArrowTnt;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import com.me.cmitems.ModBlocks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.me.cmitems.CmItems.mc;

// Tnt that spawns arrow everywhere when explode
public class ArrowTnt extends TntBlock {
    public static final Block ARROW_TNT = ModBlocks.register(
            "arrow_tnt",
            ArrowTnt::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS),
            true
    );

    public ArrowTnt(Settings settings) {
        super(settings);
    }

    public static void register() {
        System.out.println("Registered Lightning Stick");
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (getBlockFromItem(stack.getItem()) == ARROW_TNT) {
                list.add(Text.translatable("item.cmitems.arrow_tnt.tooltip"));
            }
        });
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            if (world.isReceivingRedstonePower(pos) && primeTnt(world, pos)) {
                world.removeBlock(pos, false);
            }

        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isReceivingRedstonePower(pos) && primeTnt(world, pos)) {
            world.removeBlock(pos, false);
        }

    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.getAbilities().creativeMode && (Boolean)state.get(UNSTABLE)) {
            primeTnt(world, pos);
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        } else {
            if (primeTnt(world, pos, player)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                Item item = stack.getItem();
                if (stack.isOf(Items.FLINT_AND_STEEL)) {
                    stack.damage(1, player, LivingEntity.getSlotForHand(hand));
                } else {
                    stack.decrementUnlessCreative(1, player);
                }

                player.incrementStat(Stats.USED.getOrCreateStat(item));
            } else if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                if (!serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                    player.sendMessage(Text.translatable("block.minecraft.tnt.disabled"), true);
                    return ActionResult.PASS;
                }
            }

            return ActionResult.SUCCESS;
        }
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (world instanceof ServerWorld serverWorld) {
            BlockPos blockPos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.canModifyAt(serverWorld, blockPos) && primeTnt(world, blockPos, entity instanceof LivingEntity ? (LivingEntity)entity : null)) {
                world.removeBlock(blockPos, false);
            }
        }

    }

    public static boolean primeTnt(World world, BlockPos pos) {
        return primeTnt(world, pos, (LivingEntity) null);
    }

    private static boolean primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (world instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                TntEntity tntEntity = new TntEntity(world, (double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5, igniter);
                world.spawnEntity(tntEntity);
                explode(world, tntEntity, igniter);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                world.playSound((Entity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
                return true;
            }
        }

        System.out.println("Breh");

        return false;
    }

    private static void explode(World world, double X, double Y, double Z, LivingEntity igniter) {
        List<ArrowEntity> arrows = new ArrayList<>();

        // Circle the tnt in 360 degrees
        for (int i = 0; i < 360; i++) {
            double radians = Math.toRadians(i);
            double x = X + Math.cos(radians) * 2;
            double z = Z + Math.sin(radians) * 2;
            if (mc.player == null) return;
            ArrowEntity arrow = new ArrowEntity(world, mc.player, new ItemStack(Items.ARROW), null);
            arrow.setPos(x, Y, z);
            arrow.setVelocity(Math.cos(radians), 0.5, Math.sin(radians));
            arrows.add(arrow);
        }

        for (ArrowEntity arrow : arrows) {
            world.spawnEntity(arrow);
        }
    }

}
