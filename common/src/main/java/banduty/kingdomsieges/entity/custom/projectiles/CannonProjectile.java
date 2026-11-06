package banduty.kingdomsieges.entity.custom.projectiles;

import banduty.kingdomsieges.entity.KSEntities;
import banduty.stoneycore.entity.projectile.AbstractSiegeProjectile;
import banduty.stoneycore.entity.projectile.SiegeImpact;
import banduty.stoneycore.particle.SCParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class CannonProjectile extends AbstractSiegeProjectile {
    private boolean shouldBreakBlocks = true;

    public CannonProjectile(EntityType<? extends CannonProjectile> type, Level level) {
        super(type, null, level);
    }

    public CannonProjectile(LivingEntity shooter, Level level) {
        super(KSEntities.CANNON_BALL.get(), shooter, level);
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
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SCParticles.MUZZLES_SMOKE_PARTICLE.get(),
                    getX(), getY(), getZ(), 5, 0, 0, 0, 0.0001);
        }
    }

    @Override
    protected void onSiegeImpact(ServerLevel level, BlockHitResult hit) {
        if (shouldBreakBlocks) SiegeImpact.crater(level, hit, getBaseDamage());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GENERIC_EXPLODE.value();
    }
}
