package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.AbstractSiegeProjectile;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class TrebuchetProjectile extends AbstractSiegeProjectile {
    private ImpactMode impactMode = ImpactMode.BREAK_BLOCKS;
    private StatusEffectInstance statusEffectInstance;
    private int cloudDuration;
    protected static final TrackedData<String> TEXTURE_NAME;

    static {
        TEXTURE_NAME = DataTracker.registerData(TrebuchetProjectile.class, TrackedDataHandlerRegistry.STRING);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TEXTURE_NAME, "");
    }

    public void setTextureName(String textureName) {
        this.dataTracker.set(TEXTURE_NAME, textureName);
    }

    public String getTextureName() {
        return this.dataTracker.get(TEXTURE_NAME);
    }

    public TrebuchetProjectile(EntityType<? extends PersistentProjectileEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    public TrebuchetProjectile(EntityType<TrebuchetProjectile> cannonProjectile, LivingEntity shooter, World world) {
        super(cannonProjectile, shooter, world);
    }

    public void setImpactMode(ImpactMode mode) {
        this.impactMode = mode;
    }

    public ImpactMode getImpactMode() {
        return impactMode;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            BlockDamageTracker.clean(serverWorld);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        int baseRadius = Math.max(1, (int) (getDamage() / 8));
        World world = this.getWorld();
        if (!(world instanceof ServerWorld serverWorld) || !(this.getOwner() instanceof AbstractSiegeEntity abstractSiegeEntity)) return;

        switch (getImpactMode()) {
            case BREAK_BLOCKS -> handleBlockBreaking(blockHitResult, serverWorld, abstractSiegeEntity, baseRadius);
            case SPREAD_FIRE -> handleSpreadFire(blockHitResult, serverWorld, baseRadius);
            case SPREAD_EFFECT -> handleSpreadEffect(blockHitResult, serverWorld, baseRadius);
        }

        this.inGround = true;
        this.shake = 7;
        this.setCritical(false);
        this.setPierceLevel((byte) 0);
        this.setSound(SoundEvents.ENTITY_ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.discard();
    }

    private void handleBlockBreaking(BlockHitResult blockHitResult, ServerWorld serverWorld, AbstractSiegeEntity abstractSiegeEntity, int baseRadius) {
        BlockPos centerPos = blockHitResult.getBlockPos();

        Random random = new Random();

        int sideHitX = blockHitResult.getSide().getAxis() == Direction.Axis.X ? 0 : baseRadius;
        int sideHitY = blockHitResult.getSide().getAxis() == Direction.Axis.Y ? 0 : baseRadius;
        int sideHitZ = blockHitResult.getSide().getAxis() == Direction.Axis.Z ? 0 : baseRadius;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = -sideHitX - 1; x <= sideHitX + 1; x++) {
            for (int y = -sideHitY - 1; y <= sideHitY + 1; y++) {
                for (int z = -sideHitZ - 1; z <= sideHitZ + 1; z++) {
                    int radiusExtension = random.nextFloat() < 0.1f ? 1 : 0;

                    int effectiveRadius = baseRadius + radiusExtension;
                    int i = x * x + y * y + z * z;
                    if (i <= effectiveRadius * effectiveRadius) {
                        if (random.nextFloat() < 0.199f * (effectiveRadius - baseRadius) + 0.001f) {
                            continue;
                        }

                        mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                        BlockPos pos = mutablePos.toImmutable();

                        BlockState state = serverWorld.getBlockState(pos);
                        float hardness = state.getHardness(serverWorld, pos);
                        if (state.isAir() || hardness < 0) continue;

                        BlockDamageTracker.damageBlock(serverWorld, pos, 1.0f / (40f / (float) getDamage()) * ((float) baseRadius / effectiveRadius), hardness);
                    }
                }
            }
        }

        BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(vec3d);
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
    }

    private void handleSpreadFire(BlockHitResult hitResult, ServerWorld world, int baseRadius) {
        BlockPos center = hitResult.getBlockPos();
        Random random = new Random();

        for (BlockPos pos : BlockPos.iterateOutwards(center, baseRadius, baseRadius, baseRadius)) {
            if (!world.getBlockState(pos).isAir()) continue;

            boolean canPlaceFire = false;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.offset(dir);
                BlockState neighborState = world.getBlockState(neighbor);

                if (neighborState.isOpaqueFullCube(world, neighbor) || neighborState.isBurnable()) {
                    canPlaceFire = true;
                    break;
                }
            }

            if (canPlaceFire && random.nextFloat() < 0.3f) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }
        }
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_GENERIC_EXPLODE;
    }

    private void handleSpreadEffect(BlockHitResult hitResult, ServerWorld world, int baseRadius) {
        if (statusEffectInstance == null) return;

        Vec3d center = Vec3d.ofCenter(hitResult.getBlockPos());

        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, center.x, center.y, center.z);
        cloud.setRadius(baseRadius);
        cloud.setRadiusOnUse(-0.25F);
        cloud.setWaitTime(0);
        cloud.setDuration(getCloudDuration());
        cloud.setRadiusGrowth(-0.0001F);
        cloud.addEffect(getStatusEffectInstance());

        world.spawnEntity(cloud);
    }

    public StatusEffectInstance getStatusEffectInstance() {
        return statusEffectInstance;
    }

    public void setStatusEffectInstance(StatusEffectInstance statusEffectInstance) {
        this.statusEffectInstance = statusEffectInstance;
    }

    public int getCloudDuration() {
        return cloudDuration;
    }

    public void setCloudDuration(int cloudDuration) {
        this.cloudDuration = cloudDuration;
    }

    public enum ImpactMode {
        BREAK_BLOCKS,
        SPREAD_FIRE,
        SPREAD_EFFECT
    }
}
