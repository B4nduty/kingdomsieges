package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.AbstractSiegeProjectile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        setVelocity(getVelocity().multiply(-10).multiply(0.9));
        setDamage(getDamage() * 0.9);
        if (getDamage() <= 1) this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            return;
        }

        if (this.getOwner() instanceof AbstractSiegeEntity abstractSiegeEntity) cleanInvalidBlockDamages(abstractSiegeEntity);
        else this.discard();
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
        if (!(world instanceof ServerWorld serverWorld) || !(this.getOwner() instanceof AbstractSiegeEntity abstractSiegeEntity)) return;

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

                        float previousDamage = abstractSiegeEntity.blockDamageMap.getOrDefault(pos, 0.0f);

                        float newDamage = previousDamage + (1.0f / (hardness * (60f / (float) getDamage()) * ((float) baseRadius / effectiveRadius)));

                        if (newDamage >= 1.0f) {
                            serverWorld.breakBlock(pos, true);
                            abstractSiegeEntity.blockDamageMap.remove(pos);
                            clearBlockBreakingProgress(serverWorld, pos);
                        } else {
                            abstractSiegeEntity.blockDamageMap.put(pos, newDamage);
                            sendBlockBreakingProgress(serverWorld, pos, newDamage);
                        }
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

    private void cleanInvalidBlockDamages(AbstractSiegeEntity abstractSiegeEntity) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        abstractSiegeEntity.blockDamageMap.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            BlockState state = serverWorld.getBlockState(pos);

            // If block is air (i.e., broken), clean it up
            if (state.isAir()) {
                clearBlockBreakingProgress(serverWorld, pos);
                return true;
            }
            return false;
        });
    }

    private void sendBlockBreakingProgress(ServerWorld world, BlockPos pos, float progress) {
        int stage = (int)(progress * 10); // 0–9
        if (stage > 9) stage = 9;

        for (ServerPlayerEntity player : world.getPlayers()) {
            int visualId = getBlockVisualId(pos);
            player.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(visualId, pos, stage));
        }
    }

    private void clearBlockBreakingProgress(ServerWorld world, BlockPos pos) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            int visualId = getBlockVisualId(pos);
            player.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(visualId, pos, -1));
        }
    }

    private int getBlockVisualId(BlockPos pos) {
        // Offset the ID with hash to make it unique for each block
        return this.getId() ^ Long.hashCode(pos.asLong());
    }
}
