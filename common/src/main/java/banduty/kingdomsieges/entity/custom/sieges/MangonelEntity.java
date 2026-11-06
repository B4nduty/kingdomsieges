package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.AmmoLoadingSiegeEntity;
import banduty.stoneycore.entity.siege.LoadingStage;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import banduty.stoneycore.entity.siege.SiegeProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

public class MangonelEntity extends AmmoLoadingSiegeEntity implements KSGeoSiege {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("mangonel")
            .health(60.0)
            .speed(0.075)
            .knockbackResist(265.0)
            .moveSound(KSSounds.SIEGE_ENGINE_MOVE.get())
            .reloadSound(KSSounds.ROPE_CHARGE_GN.get())
            .shootSound(KSSounds.MANGONEL_SHOOT.get())
            .cooldown(10)
            .fireDelay(3)
            .build();

    private static final List<LoadingStage> AMMO = List.of(
            LoadingStage.of(Items.STONE),
            LoadingStage.of(Items.MAGMA_BLOCK));

    private static final Map<SiegeAnimation, RawAnimation> ANIMATIONS = Map.of(
            SiegeAnimation.FIRE, RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE));
    private static final RawAnimation RELOADING = RawAnimation.begin().then("reloading", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation LOADED = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation UNLOADED = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public MangonelEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PROPERTIES.createAttributes();
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    protected List<LoadingStage> ammoTypes() { return AMMO; }

    @Override
    protected void launch(ServerLevel level, Item ammo) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(this, level);
        configure(projectile, position().add(0, 2.25, 0), flatAimDirection());
        projectile.applyAmmo(ammo);
        level.addFreshEntity(projectile);
    }

    @Nullable
    @Override
    public RawAnimation stateAnimation() {
        if (isReloading()) return RELOADING;
        return hasAmmoLoaded() ? LOADED : UNLOADED;
    }

    @Override
    public Map<SiegeAnimation, RawAnimation> triggeredAnimations() { return ANIMATIONS; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animatableInstanceCache; }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.25) : new Vec3(0.0, 0.0, 2.0);
    }

    @Override
    public Vec3 getPlayerPOV() { return new Vec3(0.0, -0.7f, 0.0); }
}
