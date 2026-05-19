package com.me.cmitems.mixin;

import com.me.cmitems.items.tools.drills.GeneralDrill;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class BlockOutlineDrillMixin {

    // @Shadow just copy WorldRenderer's fields.
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    @Shadow
    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double x, double y, double z,
                                  BlockPos blockPos, BlockState blockState, int color) {}

    @Inject(method = "renderTargetBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onRenderOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean translucent, CallbackInfo ci) {
        // Default code
        if (client.player != null && client.player.getMainHandStack().getItem() instanceof GeneralDrill drill) {
            HitResult var6 = this.client.crosshairTarget;
            if (var6 instanceof BlockHitResult blockHitResult) {
                if (blockHitResult.getType() != HitResult.Type.MISS) {
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    BlockState blockState = this.world.getBlockState(blockPos);
                    if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos)) {
                        boolean bl = RenderLayers.getBlockLayer(blockState).isTranslucent();
                        if (bl != translucent) {
                            return;
                        }

                        Vec3d vec3d = camera.getPos();
                        Boolean boolean_ = (Boolean) this.client.options.getHighContrastBlockOutline().getValue();
                        VertexConsumer vertexConsumer;
                        if (boolean_) {
                            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSecondaryBlockOutline());
                            this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, blockPos, blockState, -16777216);
                            // Adding all new blocks from the drill's 3x3 area
                            drawDrillAreaOutline(camera, matrices, drill, blockPos, vec3d, vertexConsumer);
                        }

                        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
                        int i = boolean_ ? Colors.CYAN : ColorHelper.withAlpha(102, Colors.BLACK);
                        this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, blockPos, blockState, i);
                        // Adding all new blocks from the drill's 3x3 area
                        drawDrillAreaOutline(camera, matrices, drill, blockPos, vec3d, vertexConsumer);
                        vertexConsumers.drawCurrentLayer();
                    }

                }
            }
            ci.cancel();
        }
    }

    @Unique
    private void drawDrillAreaOutline(Camera camera, MatrixStack matrices, GeneralDrill drill, BlockPos blockPos, Vec3d vec3d, VertexConsumer vertexConsumer) {
        List<BlockPos> positions = drill.getBlockAreaPosition(blockPos);
        for (BlockPos pos : positions) {
            BlockState state = this.world.getBlockState(pos);
            if (!state.isAir() && this.world.getWorldBorder().contains(pos)) {
                this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, pos, state, -16777216);
            }
        }
    }
}