package com.me.cmitems.items;

import com.me.cmitems.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class LightningStick extends BowItem {
    public static Item LIGHTNING_STICK = ModItems.register(
            "lightning_stick",
            LightningStick::new,
            new Item.Settings().maxCount(1)
    );

    public static void register() {
        System.out.println("Registered Lightning Stick");
    }

    public LightningStick(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack item) {
        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return ActionResult.PASS;
        }

        BlockHitResult hit = (BlockHitResult) user.raycast(20.0, 0.0f, false);

        BlockPos pos;
        if (hit.getType() == HitResult.Type.BLOCK) {
            // If looking at a block, use that block's position
            pos = hit.getBlockPos();
        } else {
            // Otherwise, teleport 10 blocks in the look direction
            Vec3d look = user.getRotationVector().normalize().multiply(10);
            Vec3d target = user.getEyePos().add(look);
            pos = new BlockPos((int) target.x, (int) target.y, (int) target.z);
        }

        // Spawn the lightning bolt.
        LightningEntity lightningBolt = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightningBolt.setPosition(pos.toCenterPos());
        world.spawnEntity(lightningBolt);


        return ActionResult.SUCCESS;
    }
}