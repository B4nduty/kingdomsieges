package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.LoadingStage;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import banduty.stoneycore.entity.siege.SiegeProperties;
import banduty.stoneycore.entity.siege.StagedLoadingSiegeEntity;
import banduty.stoneycore.items.SCItems;
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

import java.util.Map;

public class CannonEntity extends StagedLoadingSiegeEntity implements KSGeoSiege {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("cannon")
            .health(50.0)
            .speed(0.05)
            .knockbackResist(265.0)
            .moveSound(KSSounds.SIEGE_ENGINE_MOVE.get())
            .reloadSound(KSSounds.ROPE_CHARGE_GN.get())
            .shootSound(KSSounds.CANNON_CLOSE.get())
            .cooldown(90)
            .build();

    private static final LoadingStage[] STAGES = {
            LoadingStage.of(SCItems.BLACK_POWDER.get(), 10),
            LoadingStage.ofDamaging(KSItems.RAMROD.get()),
            LoadingStage.of(Items.STONE),
            LoadingStage.ofDamaging(KSItems.RAMROD.get()),
            LoadingStage.ofIgnition(Items.FLINT_AND_STEEL)
    };

    private static final Map<SiegeAnimation, RawAnimation> ANIMATIONS = Map.of(
            SiegeAnimation.FIRE, RawAnimation.begin().then("fire", Animation.LoopType.PLAY_ONCE));
    private static final RawAnimation LOADED = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation UNLOADED = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public CannonEntity(EntityType<? extends LivingEntity> type, Level level) {
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

        Vec3 mouth = muzzle(getYRot(), 1.4, 1.0);
        CannonProjectile projectile = new CannonProjectile(this, level);
        configure(projectile, mouth, aimDirection(0));
        level.addFreshEntity(projectile);

        level.sendParticles(ParticleTypes.SMOKE, mouth.x, mouth.y, mouth.z, 20, 0.05, 0.05, 0.05, 0.01);
        playShootSound(level);
        startCooldown();
    }

    @Nullable
    @Override
    public RawAnimation stateAnimation() { return isReadyToFire() ? LOADED : UNLOADED; }

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
