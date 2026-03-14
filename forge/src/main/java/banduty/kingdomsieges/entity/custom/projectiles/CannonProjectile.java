package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.AbstractSiegeProjectile;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class CannonProjectile extends AbstractSiegeProjectile {
    private boolean shouldBreakBlocks = true;

    public CannonProjectile(EntityType<? extends AbstractArrow> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public CannonProjectile(EntityType<CannonProjectile> cannonProjectile, LivingEntity shooter, Level level) {
        super(cannonProjectile, shooter, level);
    }

    public boolean shouldBreakBlocks() {
        return shouldBreakBlocks;
    }

    public void setShouldBreakBlocks(boolean shouldBreakBlocks) {
        this.shouldBreakBlocks = shouldBreakBlocks;
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

        if (!shouldBreakBlocks()) {
            this.discard();
            return;
        }

        Level world = this.level();
        BlockPos centerPos = blockHitResult.getBlockPos();
        if (!(world instanceof ServerLevel serverLevel) || !(this.getOwner() instanceof AbstractSiegeEntity)) return;

        Random random = new Random();

        int baseRadius = Math.max(1, (int) (getBaseDamage() / 8));
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

                        BlockState state = world.getBlockState(pos);
                        float hardness = state.getDestroySpeed(world, pos);
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

        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.discard();
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GENERIC_EXPLODE;
    }
}
