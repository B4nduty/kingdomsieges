package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.AbstractSiegeProjectile;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class TrebuchetProjectile extends AbstractSiegeProjectile {
    private ImpactMode impactMode = ImpactMode.BREAK_BLOCKS;
    private MobEffectInstance statusEffectInstance;
    private int cloudDuration;
    protected static final EntityDataAccessor<String> TEXTURE_NAME;

    static {
        TEXTURE_NAME = SynchedEntityData.defineId(TrebuchetProjectile.class, EntityDataSerializers.STRING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_NAME, "");
    }

    public void setTextureName(String textureName) {
        this.entityData.set(TEXTURE_NAME, textureName);
    }

    public String getTextureName() {
        return this.entityData.get(TEXTURE_NAME);
    }

    public TrebuchetProjectile(EntityType<? extends AbstractArrow> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public TrebuchetProjectile(EntityType<TrebuchetProjectile> cannonProjectile, LivingEntity shooter, Level level) {
        super(cannonProjectile, shooter, level);
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

        if (this.level() instanceof ServerLevel serverLevel) {
            BlockDamageTracker.clean(serverLevel);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);

        int baseRadius = Math.max(1, (int) (getBaseDamage() / 8));
        Level world = this.level();
        if (!(world instanceof ServerLevel serverLevel) || !(this.getOwner() instanceof AbstractSiegeEntity)) return;

        switch (getImpactMode()) {
            case BREAK_BLOCKS -> handleBlockBreaking(blockHitResult, serverLevel, baseRadius);
            case SPREAD_FIRE -> handleSpreadFire(blockHitResult, serverLevel, baseRadius);
            case SPREAD_EFFECT -> handleSpreadEffect(blockHitResult, serverLevel, baseRadius);
        }

        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.discard();
    }

    private void handleBlockBreaking(BlockHitResult blockHitResult, ServerLevel serverLevel, int baseRadius) {
        BlockPos centerPos = blockHitResult.getBlockPos();

        Random random = new Random();

        int sideHitX = blockHitResult.getDirection().getAxis() == Direction.Axis.X ? 0 : baseRadius;
        int sideHitY = blockHitResult.getDirection().getAxis() == Direction.Axis.Y ? 0 : baseRadius;
        int sideHitZ = blockHitResult.getDirection().getAxis() == Direction.Axis.Z ? 0 : baseRadius;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
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
                        BlockPos pos = mutablePos.immutable();

                        BlockState state = serverLevel.getBlockState(pos);
                        float hardness = state.getDestroySpeed(serverLevel, pos);
                        if (state.isAir() || hardness < 0) continue;

                        BlockDamageTracker.damageBlock(serverLevel, pos, 1.0f / (40f / (float) getBaseDamage()) * ((float) baseRadius / effectiveRadius), hardness);
                    }
                }
            }
        }

        BlockState blockState = this.level().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.level(), blockState, blockHitResult, this);
        Vec3 vec3d = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3d);
        Vec3 vec3d2 = vec3d.normalize().scale(0.05F);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
    }

    private void handleSpreadFire(BlockHitResult hitResult, ServerLevel serverLevel, int baseRadius) {
        BlockPos center = hitResult.getBlockPos();
        Random random = new Random();

        for (BlockPos pos : BlockPos.withinManhattan(center, baseRadius, baseRadius, baseRadius)) {
            if (!serverLevel.getBlockState(pos).isAir()) continue;

            boolean canPlaceFire = false;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                BlockState neighborState = serverLevel.getBlockState(neighbor);

                if (neighborState.isSolidRender(serverLevel, neighbor) || neighborState.ignitedByLava()) {
                    canPlaceFire = true;
                    break;
                }
            }

            if (canPlaceFire && random.nextFloat() < 0.3f) {
                serverLevel.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            }
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    private void handleSpreadEffect(BlockHitResult hitResult, ServerLevel serverLevel, int baseRadius) {
        if (statusEffectInstance == null) return;

        Vec3 center = Vec3.atCenterOf(hitResult.getBlockPos());

        AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, center.x, center.y, center.z);
        cloud.setRadius(baseRadius);
        cloud.setRadiusOnUse(-0.25F);
        cloud.setWaitTime(0);
        cloud.setDuration(getCloudDuration());
        cloud.setRadiusPerTick(-0.0001F);
        cloud.addEffect(getStatusEffectInstance());

        serverLevel.addFreshEntity(cloud);
    }

    public MobEffectInstance getStatusEffectInstance() {
        return statusEffectInstance;
    }

    public void setStatusEffectInstance(MobEffectInstance statusEffectInstance) {
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
