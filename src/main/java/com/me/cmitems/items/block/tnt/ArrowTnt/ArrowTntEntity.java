package com.me.cmitems.items.block.tnt.ArrowTnt;

import com.me.cmitems.ModSimpleEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.me.cmitems.CmItems.mc;

public class ArrowTntEntity extends TntEntity {

    public static final EntityType<ArrowTntEntity> ARROW_TNT_TYPE = ModSimpleEntity.register(
            "arrow_tnt_entity",
            ArrowTntEntity::new,
            SpawnGroup.MISC,
            1f,
            1f
    );

    private static final boolean ShouldBeBurningArrows = true;
    private static final boolean ShouldHaveGravity = false;

    private static final short DEFAULT_FUSE = 80;
    private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
    private static final BlockState DEFAULT_BLOCK_STATE = ArrowTnt.ARROW_TNT.getDefaultState();
    private static final String BLOCK_STATE_NBT_KEY = "block_state";
    public static final String FUSE_NBT_KEY = "fuse";
    private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
    private static final ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }

        @Override
        public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
        }
    };

    @Nullable
    private LivingEntity causingEntity;
    private boolean teleported;
    private float explosionPower;

    public ArrowTntEntity(EntityType<? extends TntEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 4.0F;
        this.intersectionChecked = true;
        this.setBlockState(DEFAULT_BLOCK_STATE);
    }

    public ArrowTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(ARROW_TNT_TYPE, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * 6.2831854820251465;
        this.setVelocity(-Math.sin(d) * 0.02, 0.20000000298023224, -Math.cos(d) * 0.02);
        this.setFuse(80);
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        this.causingEntity = igniter;
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected double getGravity() {
        return ShouldHaveGravity ? 0.04 : 0.0;
    }

    @Override
    protected void applyGravity() {
        if (!ShouldHaveGravity) return;
        double d = this.getFinalGravity();
        if (d != 0.0) {
            this.setVelocity(this.getVelocity().add(0.0, -d, 0.0));
        }

    }

    @Override
    public void tick() {
        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.tickBlockCollision();
        if (ShouldHaveGravity) this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.isOnGround() && ShouldHaveGravity) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.getWorld().isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.getWorld().isClient) {
                this.getWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void explode() {
        World world = this.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {

                for (ArrowEntity arrow : this.getArrows(world)) {
                    world.spawnEntity(arrow);
                }
            }
        }
    }

    /**
     * Spawns arrows in a spherical pattern around the TNT explosion point, flying outward in all directions.
     * @param world
     * @return
     */
    private List<ArrowEntity> getArrows(World world) {
        List<ArrowEntity> arrows = new ArrayList<>();
        if (mc.player == null) return arrows;

        int horizontalSteps = 18;
        int verticalSteps = 20;   // 18 * 20 = 360 arrows
        double radius = 1.5;      // Spawn distance from center
        double speed = 1.2;

        for (int i = 0; i < verticalSteps; i++) {
            double phi = Math.PI * (double) i / (verticalSteps - 1);

            for (int j = 0; j < horizontalSteps; j++) {
                double theta = 2.0 * Math.PI * (double) j / horizontalSteps;

                // Calculate directional vectors
                double vx = Math.sin(phi) * Math.cos(theta);
                double vy = Math.cos(phi);
                double vz = Math.sin(phi) * Math.sin(theta);

                // Calculate exact spawn position
                double x = this.getX() + vx * radius;
                double y = this.getY() + vy * radius + 0.5;
                double z = this.getZ() + vz * radius;

                // --- COLLISION TEST ---
                BlockPos spawnPos = new BlockPos((int)x, (int)y, (int)z);
                // Check if the block at the spawn position is air or non-solid
                if (!world.getBlockState(spawnPos).isAir()) {
                    continue; // Skip this arrow if it's inside a block
                }

                ArrowEntity arrow = new ArrowEntity(world, mc.player, new ItemStack(Items.ARROW), null);
                arrow.setPos(x, y, z);
                arrow.setVelocity(vx * speed, vy * speed, vz * speed);
                arrow.setOnFireFor(ShouldBeBurningArrows ? 15 : 0); // 15s of fire if should burn
                arrow.setDamage(5); // 2.5 hearts of damage

                arrows.add(arrow);
            }
        }
        return arrows;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.explosionPower != 4.0F) {
            nbt.putFloat("explosion_power", this.explosionPower);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.explosionPower = MathHelper.clamp(nbt.getFloat("explosion_power", 4.0F), 0.0F, 128.0F);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.causingEntity;
    }

    @Override
    public void copyFrom(Entity original) {
        super.copyFrom(original);
        if (original instanceof ArrowTntEntity atEntity) {
            this.causingEntity = atEntity.causingEntity;
        }
    }

    private void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    @Nullable
    @Override
    public Entity teleportTo(TeleportTarget teleportTarget) {
        Entity entity = super.teleportTo(teleportTarget);
        if (entity instanceof ArrowTntEntity atEntity) {
            atEntity.setTeleported(true);
        }
        return entity;
    }
}