package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.AmmoLoadingSiegeEntity;
import banduty.stoneycore.entity.siege.LoadingStage;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import banduty.stoneycore.entity.siege.SiegeProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

public class TrebuchetEntity extends AmmoLoadingSiegeEntity implements KSGeoSiege {

    private static final float ROTATION_STEP = 45.0f;

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("trebuchet")
            .health(200.0)
            .speed(0.0)
            .knockbackResist(265.0)
            .reloadSound(KSSounds.ROPE_CHARGE_GN.get())
            .shootSound(KSSounds.TREBUCHET_SHOOT.get())
            .reloadSoundRange(15.0)
            .shootSoundRange(75.0)
            .cooldown(60)
            .fireDelay(8)
            .build();

    private static final List<LoadingStage> AMMO = List.of(
            LoadingStage.of(Items.STONE),
            LoadingStage.of(Items.MAGMA_BLOCK),
            LoadingStage.of(Items.ROTTEN_FLESH, 9));

    private static final Map<SiegeAnimation, RawAnimation> ANIMATIONS = Map.of(
            SiegeAnimation.FIRE, RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE));
    private static final RawAnimation RELOADING = RawAnimation.begin().then("reload", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation LOADED = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation UNLOADED = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public TrebuchetEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PROPERTIES.createAttributes();
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public boolean canAddPassenger(Entity entity) { return false; }

    @Override
    public double getVelocity(Entity passenger) { return 0.0; }

    @Override
    protected List<LoadingStage> ammoTypes() { return AMMO; }

    @Override
    protected InteractionResult operate(Player player, ItemStack stack, ServerLevel level) {
        if (getCooldown() > 0) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) {
            setFacing(getYRot() + ROTATION_STEP);
            return InteractionResult.SUCCESS;
        }

        return super.operate(player, stack, level);
    }

    @Override
    protected void launch(ServerLevel level, Item ammo) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(this, level);
        configure(projectile, position().add(0, 12.0, 0), flatAimDirection());
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
    public Vec3 getPassengerOffset(Entity entity) { return Vec3.ZERO; }

    @Override
    public Vec3 getPlayerPOV() { return Vec3.ZERO; }
}
