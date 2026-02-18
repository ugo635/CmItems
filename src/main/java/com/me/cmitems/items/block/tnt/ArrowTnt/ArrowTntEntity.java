package com.me.cmitems.items.block.tnt.ArrowTnt;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.FluidState;
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

import java.util.Optional;

public class ArrowTntEntity extends TntEntity {
    private static final TrackedData<Integer> FUSE;
    private static final TrackedData<BlockState> BLOCK_STATE;
    private static final short DEFAULT_FUSE = 80;
    private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
    private static final BlockState DEFAULT_BLOCK_STATE;
    private static final String BLOCK_STATE_NBT_KEY = "block_state";
    public static final String FUSE_NBT_KEY = "fuse";
    private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
    private static final ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR;
    @Nullable
    private LivingEntity causingEntity;
    private boolean teleported;
    private float explosionPower;

    public ArrowTntEntity(EntityType<? extends TntEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 4.0F;
        this.intersectionChecked = true;
    }

    public ArrowTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(EntityType.TNT, world);
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
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FUSE, 80);
        builder.add(BLOCK_STATE, DEFAULT_BLOCK_STATE);
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
        return 0.04;
    }

    @Override
    public void tick() {
        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.tickBlockCollision();
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.isOnGround()) {
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
        World var2 = this.getWorld();
        if (var2 instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getBodyY(0.0625), this.getZ(), this.explosionPower, false, World.ExplosionSourceType.TNT);
            }
        }

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        nbt.putShort("fuse", (short)this.getFuse());
        nbt.put("block_state", BlockState.CODEC, registryOps, this.getBlockState());
        if (this.explosionPower != 4.0F) {
            nbt.putFloat("explosion_power", this.explosionPower);
        }

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        this.setFuse(nbt.getShort("fuse", (short)80));
        this.setBlockState((BlockState)nbt.get("block_state", BlockState.CODEC, registryOps).orElse(DEFAULT_BLOCK_STATE));
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

    @Override
    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    @Override
    public int getFuse() {
        return (Integer)this.dataTracker.get(FUSE);
    }

    @Override
    public void setBlockState(BlockState state) {
        this.dataTracker.set(BLOCK_STATE, state);
    }

    @Override
    public BlockState getBlockState() {
        return (BlockState)this.dataTracker.get(BLOCK_STATE);
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

    static {
        FUSE = DataTracker.registerData(ArrowTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
        BLOCK_STATE = DataTracker.registerData(ArrowTntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
        DEFAULT_BLOCK_STATE = Blocks.TNT.getDefaultState();
        TELEPORTED_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
            @Override
            public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                return false; // No explosion, we just want arrow
            }

            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
            }
        };
    }

}
