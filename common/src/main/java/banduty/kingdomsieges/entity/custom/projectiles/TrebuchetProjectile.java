package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.kingdomsieges.entity.KSEntities;
import banduty.stoneycore.entity.projectile.AbstractSiegeProjectile;
import banduty.stoneycore.entity.projectile.SiegeImpact;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TrebuchetProjectile extends AbstractSiegeProjectile {

    public enum ImpactMode { BREAK_BLOCKS, SPREAD_FIRE, SPREAD_EFFECT }

    private static final EntityDataAccessor<String> TEXTURE_NAME =
            SynchedEntityData.defineId(TrebuchetProjectile.class, EntityDataSerializers.STRING);

    private ImpactMode impactMode = ImpactMode.BREAK_BLOCKS;
    @Nullable
    private MobEffectInstance cloudEffect;
    private int cloudDuration;

    public TrebuchetProjectile(EntityType<? extends TrebuchetProjectile> type, Level level) {
        super(type, null, level);
    }

    public TrebuchetProjectile(LivingEntity shooter, Level level) {
        super(KSEntities.TREBUCHET_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE_NAME, "");
    }

    public String getTextureName() { return entityData.get(TEXTURE_NAME); }

    public void setTextureName(String textureName) { entityData.set(TEXTURE_NAME, textureName); }

    public ImpactMode getImpactMode() { return impactMode; }

    public void applyAmmo(Item ammo) {
        if (ammo == Items.MAGMA_BLOCK) {
            impactMode = ImpactMode.SPREAD_FIRE;
            setTextureName("magma");
        } else if (ammo == Items.ROTTEN_FLESH) {
            impactMode = ImpactMode.SPREAD_EFFECT;
            cloudEffect = new MobEffectInstance(MobEffects.POISON, 100, 0, true, true, true);
            cloudDuration = 600;
            setTextureName("rotten_flesh");
        } else {
            impactMode = ImpactMode.BREAK_BLOCKS;
            setTextureName("stone");
        }
    }

    @Override
    protected void onSiegeImpact(ServerLevel level, BlockHitResult hit) {
        switch (impactMode) {
            case BREAK_BLOCKS -> SiegeImpact.crater(level, hit, getBaseDamage());
            case SPREAD_FIRE -> spreadFire(level, hit);
            case SPREAD_EFFECT -> spreadEffect(level, hit);
        }
    }

    private void spreadFire(ServerLevel level, BlockHitResult hit) {
        int radius = SiegeImpact.radiusFor(getBaseDamage());

        for (BlockPos pos : BlockPos.withinManhattan(hit.getBlockPos(), radius, radius, radius)) {
            if (!level.getBlockState(pos).isAir()) continue;
            if (level.random.nextFloat() >= 0.3f) continue;

            boolean canBurn = false;
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighbor);
                if (neighborState.isSolidRender(level, neighbor) || neighborState.ignitedByLava()) {
                    canBurn = true;
                    break;
                }
            }

            if (canBurn) level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        }
    }

    private void spreadEffect(ServerLevel level, BlockHitResult hit) {
        if (cloudEffect == null) return;

        Vec3 center = Vec3.atCenterOf(hit.getBlockPos());
        AreaEffectCloud cloud = new AreaEffectCloud(level, center.x, center.y, center.z);
        cloud.setRadius(SiegeImpact.radiusFor(getBaseDamage()));
        cloud.setRadiusOnUse(-0.25F);
        cloud.setWaitTime(0);
        cloud.setDuration(cloudDuration);
        cloud.setRadiusPerTick(-0.0001F);
        cloud.addEffect(cloudEffect);
        level.addFreshEntity(cloud);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GENERIC_EXPLODE.value();
    }
}
