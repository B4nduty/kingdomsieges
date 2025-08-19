package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.AbstractSiegeProjectile;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

public class CannonProjectile extends AbstractSiegeProjectile {
    private boolean shouldBreakBlocks = true;

    public CannonProjectile(EntityType<? extends PersistentProjectileEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    public CannonProjectile(EntityType<CannonProjectile> cannonProjectile, LivingEntity shooter, World world) {
        super(cannonProjectile, shooter, world);
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

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            BlockDamageTracker.clean(serverWorld);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (!shouldBreakBlocks()) {
            this.discard();
            return;
        }

        World world = this.getWorld();
        BlockPos centerPos = blockHitResult.getBlockPos();
        if (!(world instanceof ServerWorld serverWorld) || !(this.getOwner() instanceof AbstractSiegeEntity)) return;

        Random random = new Random();

        int baseRadius = Math.max(1, (int) (getDamage() / 8));
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

                        BlockState state = world.getBlockState(pos);
                        float hardness = state.getHardness(world, pos);
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

        this.inGround = true;
        this.shake = 7;
        this.setCritical(false);
        this.setPierceLevel((byte) 0);
        this.setSound(SoundEvents.ENTITY_ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.discard();
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_GENERIC_EXPLODE;
    }
}
