package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.combat.melee.SCDamageType;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.LoadingStage;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import banduty.stoneycore.items.SCItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

public class CannonEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("cannon")
            .health(50.0)
            .speed(0.05)
            .knockbackResist(265.0)
            .moveSound(ModSounds.SIEGE_ENGINE_MOVE)
            .reloadSound(ModSounds.ROPE_CHARGE_GN)
            .shootSound(ModSounds.CANNON_CLOSE)
            .build();

    private static final LoadingStage[] LOAD_STAGES = {
            LoadingStage.of(SCItems.BLACK_POWDER, 10),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.STONE),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.ofDamaging(Items.FLINT_AND_STEEL)
    };

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation fireAnim = RawAnimation.begin().then("fire", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loadedAnim = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloadedAnim = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    public CannonEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.05)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public InteractionResult handleSiegeInteraction(Player player, InteractionHand hand, ServerLevel serverLevel) {
        if (getFirstPassenger() != null) return InteractionResult.FAIL;

        ItemStack stack = player.getItemInHand(hand);
        int stage = getLoadStage();

        if (stage >= LOAD_STAGES.length) return InteractionResult.SUCCESS;
        if (getCooldown() > 0) return InteractionResult.SUCCESS;

        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.isEmpty() && canAddPassenger(player) && !player.isShiftKeyDown()) {
            player.startRiding(this);
            setOwner(player);
            return InteractionResult.SUCCESS;
        }

        LoadingStage required = LOAD_STAGES[stage];

        if (!required.matches(stack.getItem())) {
            player.displayClientMessage(
                    Component.translatable("siege.loading.next", required.item().getDefaultInstance().getHoverName()),
                    true);
            return InteractionResult.SUCCESS;
        }

        if (required.item() == Items.FLINT_AND_STEEL) {
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
            fireCannon(serverLevel);
            setOwner(player);
            setLoadStage(0);
            return InteractionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            if (required.consumesItem() && stack.getCount() < required.amount()) {
                player.displayClientMessage(
                        Component.translatable("siege.loading.need_amount", required.amount(),
                                required.item().getDefaultInstance().getHoverName()), true);
                return InteractionResult.FAIL;
            }

            if (required.consumesItem()) stack.shrink(required.amount());
            if (required.damagesItem()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
        }

        setLoadStage(stage + 1);

        if (getLoadStage() == LOAD_STAGES.length - 1) {
            triggerAnimation("loaded");
        }

        return InteractionResult.SUCCESS;
    }

    private void fireCannon(ServerLevel serverLevel) {
        triggerAnimation("fire");

        CannonProjectile projectile = new CannonProjectile(ModEntities.CANNON_BALL, this, serverLevel);
        Vec3 mouthPos = getMouthOffset();
        projectile.setPos(mouthPos.x, mouthPos.y, mouthPos.z);

        Vec3 direction = calculateProjectileDirection();
        projectile.setDeltaMovement(direction.scale(getProjectileSpeed() / 20.0));
        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageType.BLUDGEONING);
        projectile.setOwner(this);

        serverLevel.addFreshEntity(projectile);

        serverLevel.sendParticles(ParticleTypes.SMOKE, mouthPos.x, mouthPos.y, mouthPos.z,
                20, 0.05, 0.05, 0.05, 0.01);

        playShootSound(serverLevel);

        setLoadStage(0);
        setCooldown(90);
    }

    private Vec3 calculateProjectileDirection() {
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float pitchOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;

        float adjustedYaw = getVisualRotationYInDegrees() + yawOffset;
        float adjustedPitch = getXRot() + pitchOffset;

        float yawRad = (float) Math.toRadians(adjustedYaw);
        float pitchRad = (float) Math.toRadians(adjustedPitch);

        return new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();
    }

    private Vec3 getMouthOffset() {
        float yawRad = (float) Math.toRadians(getVisualRotationYInDegrees());
        double forward = 1.4;
        return new Vec3(
                getX() - Math.sin(yawRad) * forward,
                getY() + 1.0 - Math.sin(Math.toRadians(getXRot())),
                getZ() + Math.cos(yawRad) * forward
        );
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        if (getLoadStage() != 4 && getLoadStage() != 5 && getCooldown() <= 10) {
            triggerAnimation("unloaded");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", 0, state -> {
            if (state.isCurrentAnimation(fireAnim)) return PlayState.CONTINUE;
            state.setAnimation(getLoadStage() >= 4 ? loadedAnim : unloadedAnim);
            return PlayState.CONTINUE;
        }).triggerableAnim("fire", fireAnim)
                .triggerableAnim("loaded", loadedAnim)
                .triggerableAnim("unloaded", unloadedAnim));
    }

    @Override
    public void triggerAnimation(String name) {
        switch (name) {
            case "fire" -> triggerAnim("anim_controller", "fire");
            case "loaded" -> triggerAnim("anim_controller", "loaded");
            case "unloaded" -> triggerAnim("anim_controller", "unloaded");
        }
    }

    @Override
    public void stopAnimation(String name) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animatableInstanceCache; }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.5) : new Vec3(0.5, 0.0, 1.0);
    }

    @Override
    public Vec3 getPlayerPOV() { return Vec3.ZERO; }
}