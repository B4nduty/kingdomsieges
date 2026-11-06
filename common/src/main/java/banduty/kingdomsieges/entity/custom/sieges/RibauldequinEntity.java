package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.LoadingStage;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import banduty.stoneycore.entity.siege.SiegeProperties;
import banduty.stoneycore.entity.siege.StagedLoadingSiegeEntity;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.particle.SCParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RibauldequinEntity extends StagedLoadingSiegeEntity implements KSGeoSiege {

    private static final int BARRELS = 5;
    private static final int TICKS_BETWEEN_SHOTS = 3;
    private static final float[] BARREL_YAWS = {-45.0f, -22.5f, 0f, 22.5f, 45.0f};

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("ribauldequin")
            .health(60.0)
            .speed(0.04)
            .knockbackResist(265.0)
            .moveSound(KSSounds.SIEGE_ENGINE_MOVE.get())
            .shootSound(KSSounds.CANNON_CLOSE.get())
            .shootSoundRange(200.0)
            .cooldown(90)
            .build();

    private static final LoadingStage[] STAGES = buildStages();

    private static LoadingStage[] buildStages() {
        List<LoadingStage> stages = new ArrayList<>();
        for (int barrel = 0; barrel < BARRELS; barrel++) {
            stages.add(LoadingStage.of(SCItems.BLACK_POWDER.get(), 2));
            stages.add(LoadingStage.ofDamaging(KSItems.RAMROD.get()));
            stages.add(LoadingStage.of(Items.IRON_NUGGET));
            stages.add(LoadingStage.ofDamaging(KSItems.RAMROD.get()));
        }
        stages.add(LoadingStage.ofIgnition(Items.FLINT_AND_STEEL));
        return stages.toArray(new LoadingStage[0]);
    }

    private static final Map<SiegeAnimation, RawAnimation> ANIMATIONS = Map.of(
            SiegeAnimation.FIRE, RawAnimation.begin().then("ribauldfire", Animation.LoopType.PLAY_ONCE));
    private static final RawAnimation LOADED = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation UNLOADED = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private int shotsRemaining;
    private int ticksToNextShot;

    public RibauldequinEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PROPERTIES.createAttributes();
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    protected LoadingStage[] loadingStages() { return STAGES; }

    @Override
    protected void fire(ServerLevel level, Player operator) {
        playAnimation(SiegeAnimation.FIRE);
        shotsRemaining = BARRELS;
        ticksToNextShot = 0;
        startCooldown();
    }

    @Override
    protected void onSiegeTick(ServerLevel level) {
        if (shotsRemaining <= 0) return;

        if (ticksToNextShot > 0) {
            ticksToNextShot--;
            return;
        }

        fireBarrel(level, BARREL_YAWS[BARRELS - shotsRemaining]);
        shotsRemaining--;
        ticksToNextShot = TICKS_BETWEEN_SHOTS - 1;
        if (shotsRemaining == 0) setCooldown(0);
    }

    private void fireBarrel(ServerLevel level, float yawOffset) {
        float barrelYaw = getYRot() + yawOffset;
        Vec3 direction = aimDirection(yawOffset);
        Vec3 mouth = muzzle(barrelYaw, 1.7, 1.25);

        CannonProjectile projectile = new CannonProjectile(this, level);
        configure(projectile, mouth, direction);
        projectile.setShouldBreakBlocks(false);
        level.addFreshEntity(projectile);

        level.sendParticles(ParticleTypes.SMOKE, mouth.x, mouth.y, mouth.z, 20, 0.05, 0.05, 0.05, 0.01);
        playShootSound(level);

        particleTrail(level, direction, mouth, SCParticles.MUZZLES_SMOKE_PARTICLE.get(), 20, 0.2f, 0.0005f, 5);
        particleTrail(level, direction, mouth, SCParticles.MUZZLES_FLASH_PARTICLE.get(), 1, 0f, 0.1f, 6);
    }

    private static void particleTrail(ServerLevel level, Vec3 direction, Vec3 start, ParticleOptions particle,
                                      int count, float delta, float speed, int length) {
        for (int i = 0; i < length; i++) {
            Vec3 p = start.add(direction.scale(i));
            level.sendParticles(particle, p.x, p.y, p.z, count, delta, delta, delta, speed);
        }
    }

    @Nullable
    @Override
    public RawAnimation stateAnimation() { return isReadyToFire() ? LOADED : UNLOADED; }

    @Override
    public double animationSpeed() { return 4.0; }

    @Override
    public Map<SiegeAnimation, RawAnimation> triggeredAnimations() { return ANIMATIONS; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animatableInstanceCache; }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.5) : new Vec3(0.5, 0.0, 1.0);
    }

    @Override
    public Vec3 getPlayerPOV() { return Vec3.ZERO; }
}
