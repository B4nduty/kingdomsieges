package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.combat.melee.SCDamageType;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.LoadingStage;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MangonelEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("mangonel")
            .health(60.0)
            .speed(0.075)
            .knockbackResist(265.0)
            .moveSound(ModSounds.SIEGE_ENGINE_MOVE)
            .reloadSound(ModSounds.ROPE_CHARGE_GN)
            .shootSound(ModSounds.MANGONEL_SHOOT)
            .moveSoundDelay(150)
            .moveSoundRange(30.0)
            .reloadSoundRange(15.0)
            .shootSoundRange(75.0)
            .build();

    private static final LoadingStage[] AMMO_LOADS = {
            LoadingStage.of(Items.STONE),
            LoadingStage.of(Items.MAGMA_BLOCK)
    };

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation shootAnim = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation reloadingAnim = RawAnimation.begin().then("reloading", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loadedAnim = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloadedAnim = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    public MangonelEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.075)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public InteractionResult handleSiegeInteraction(Player player, InteractionHand hand, ServerLevel serverLevel) {
        if (getFirstPassenger() != null) return InteractionResult.FAIL;
        if (getCooldown() > 0) return InteractionResult.FAIL;

        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.isEmpty() && canAddPassenger(player) && !player.isShiftKeyDown()) {
            player.startRiding(this);
            setOwner(player);
            return InteractionResult.SUCCESS;
        }

        if (!hasAmmoLoaded()) {
            LoadingStage match = findMatchingAmmo(itemStack);

            if (match == null) {
                showAmmoListMessage(player);
                return InteractionResult.FAIL;
            }

            if (!player.isCreative()) {
                if (itemStack.getCount() < match.amount()) {
                    player.displayClientMessage(
                            Component.translatable("siege.loading.need_amount", match.amount(),
                                    match.item().getDefaultInstance().getHoverName()), true);
                    return InteractionResult.FAIL;
                }
                itemStack.shrink(match.amount());
            }

            setAmmoLoaded(match.item().toString());
            setReloadTime(getBaseReload());
            triggerAnimation("reloading");
            playReloadSound(serverLevel);
            return InteractionResult.SUCCESS;
        }

        if (isReloadComplete()) {
            triggerAnimation("shoot");
            setCooldown(10);
            playShootSound(serverLevel);
            setOwner(player);
        }

        return InteractionResult.SUCCESS;
    }

    private LoadingStage findMatchingAmmo(ItemStack stack) {
        for (LoadingStage stage : AMMO_LOADS) {
            if (stack.is(stage.item())) return stage;
        }
        return null;
    }

    private void showAmmoListMessage(Player player) {
        Component ammoList = AMMO_LOADS[0].item().getDefaultInstance().getHoverName()
                .copy().append(", ").append(AMMO_LOADS[1].item().getDefaultInstance().getHoverName());
        player.displayClientMessage(Component.translatable("siege.loading.need_one_of", ammoList), true);
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        if (hasAmmoLoaded()) {
            if (isReloadComplete() && getCooldown() <= 0) {
                triggerAnimation("loaded");
            }
            if (getCooldown() == 7) {
                fireMangonel(serverLevel);
            }
        } else if (getCooldown() <= 0 && isReloadComplete()) {
            triggerAnimation("unloaded");
        }
    }

    private void fireMangonel(ServerLevel serverLevel) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(ModEntities.TREBUCHET_PROJECTILE, this, serverLevel);
        projectile.setPos(this.getX(), this.getY() + 2.25, this.getZ());

        double blocksPerTick = getProjectileSpeed() / 20.0;
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = this.getVisualRotationYInDegrees() + yawOffset;
        float yawRad = (float) Math.toRadians(adjustedYaw);

        Vec3 direction = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        projectile.setDeltaMovement(direction.scale(blocksPerTick));

        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageType.BLUDGEONING);
        projectile.setOwner(this);

        Item loadedItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(getAmmoLoaded()));

        if (loadedItem == Items.STONE) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.BREAK_BLOCKS);
            projectile.setTextureName("stone");
        } else if (loadedItem == Items.MAGMA_BLOCK) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_FIRE);
            projectile.setTextureName("magma");
        }

        serverLevel.addFreshEntity(projectile);
        setAmmoLoaded("");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", state -> PlayState.STOP)
                .triggerableAnim("shoot", shootAnim)
                .triggerableAnim("reloading", reloadingAnim)
                .setAnimationSpeed(100.0 / getBaseReload())
                .triggerableAnim("loaded", loadedAnim)
                .triggerableAnim("unloaded", unloadedAnim));
    }

    @Override
    public void triggerAnimation(String name) {
        switch (name) {
            case "shoot" -> triggerAnim("anim_controller", "shoot");
            case "reloading" -> triggerAnim("anim_controller", "reloading");
            case "loaded" -> triggerAnim("anim_controller", "loaded");
            case "unloaded" -> triggerAnim("anim_controller", "unloaded");
        }
    }

    @Override
    public void stopAnimation(String name) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.25) : new Vec3(0.0, 0.0, 2.0);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, -0.7f, 0.0);
    }
}