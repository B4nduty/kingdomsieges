package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.LoadingStage;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.particle.ModParticles;
import banduty.stoneycore.util.SCDamageCalculator;
import net.minecraft.core.particles.ParticleOptions;
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

import java.util.ArrayList;
import java.util.List;

public class RibauldequinEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("ribauldequin")
            .health(60.0)
            .speed(0.04)
            .knockbackResist(265.0)
            .moveSound(ModSounds.SIEGE_ENGINE_MOVE)
            .shootSound(ModSounds.CANNON_CLOSE)
            .moveSoundDelay(150)
            .moveSoundRange(30.0)
            .shootSoundRange(200.0)
            .build();

    private static final LoadingStage[] LOAD_STAGES = {
            // Barrel 1
            LoadingStage.of(SCItems.BLACK_POWDER, 2),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.IRON_NUGGET),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            // Barrel 2
            LoadingStage.of(SCItems.BLACK_POWDER, 2),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.IRON_NUGGET),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            // Barrel 3
            LoadingStage.of(SCItems.BLACK_POWDER, 2),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.IRON_NUGGET),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            // Barrel 4
            LoadingStage.of(SCItems.BLACK_POWDER, 2),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.IRON_NUGGET),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            // Barrel 5
            LoadingStage.of(SCItems.BLACK_POWDER, 2),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            LoadingStage.of(Items.IRON_NUGGET),
            LoadingStage.ofDamaging(KSItems.RAMROD),
            // Ignition
            LoadingStage.ofDamaging(Items.FLINT_AND_STEEL)
    };

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation fireAnim = RawAnimation.begin().then("ribauldfire", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loadedAnim = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloadedAnim = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    private int nextShotTick = -1;
    private int shotsRemaining = 0;
    private final float[] yawOffsets = {-45.0f, -22.5f, 0f, 22.5f, 45.0f};

    public RibauldequinEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.04)
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
                    Component.translatable("siege.loading.next",
                            required.item().getDefaultInstance().getHoverName()), true);
            return InteractionResult.SUCCESS;
        }

        if (required.item() == Items.FLINT_AND_STEEL) {
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
            startFiringSequence();
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

        if (stage + 1 == LOAD_STAGES.length - 1) {
            triggerAnimation("loaded");
        }

        return InteractionResult.SUCCESS;
    }

    private void startFiringSequence() {
        triggerAnimation("fire");
        shotsRemaining = 5;
        nextShotTick = 0;
        setCooldown(90);
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        if (getLoadStage() < LOAD_STAGES.length - 1 && getCooldown() <= 0) {
            triggerAnimation("unloaded");
        }

        if (shotsRemaining > 0) {
            if (nextShotTick <= 0) {
                fireSingleShot(serverLevel, yawOffsets[5 - shotsRemaining]);
                shotsRemaining--;
                nextShotTick = 2;
                if (shotsRemaining == 0) setCooldown(0);
            } else {
                nextShotTick--;
            }
        }
    }

    private void fireSingleShot(ServerLevel serverLevel, float angleOffset) {
        CannonProjectile projectile = new CannonProjectile(ModEntities.CANNON_BALL, this, serverLevel);

        float baseYaw = getVisualRotationYInDegrees();
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = baseYaw + angleOffset + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float pitchOffset = getXRot() + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;

        float yawRad = (float) Math.toRadians(yawOffset);
        float pitchRad = (float) Math.toRadians(pitchOffset);

        Vec3 direction = new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();

        double blocksPerTick = getProjectileSpeed() / 20.0;
        float totalYaw = (float) Math.toRadians(baseYaw + angleOffset);

        double forward = 1.7;
        double x = getX() - Math.sin(totalYaw) * forward;
        double y = getY() + 1.25 - Math.sin(Math.toRadians(getXRot()));
        double z = getZ() + Math.cos(totalYaw) * forward;

        projectile.setPos(x, y, z);
        projectile.setDeltaMovement(direction.scale(blocksPerTick));
        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);
        projectile.setShouldBreakBlocks(false);

        serverLevel.addFreshEntity(projectile);
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 20, 0.05, 0.05, 0.05, 0.01);

        playShootSound(serverLevel);

        Vec3 mouthPos = new Vec3(x, y, z);
        spawnParticleTrail(serverLevel, direction.normalize(), mouthPos, ModParticles.MUZZLES_SMOKE_PARTICLE, 20, 0.2f, 0.0005f, 5);
        spawnParticleTrail(serverLevel, direction.normalize(), mouthPos, ModParticles.MUZZLES_FLASH_PARTICLE, 1, 0f, 0.1f, 6);
    }

    private void spawnParticleTrail(ServerLevel serverLevel, Vec3 direction, Vec3 pos,
                                    ParticleOptions particle, int count, float delta, float spread, int distance) {
        List<Vec3> trailPositions = new ArrayList<>();
        for (int i = 0; i < distance; i++) {
            trailPositions.add(pos.add(direction.scale(i)));
        }
        for (Vec3 blockPos : trailPositions) {
            serverLevel.sendParticles(particle, blockPos.x, blockPos.y, blockPos.z, count, delta, delta, delta, spread);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("fire", fireAnim)
                .setAnimationSpeed(4.0 / Math.max(1, nextShotTick + 1))
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
    public void stopAnimation(String name) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.5) : new Vec3(0.5, 0.0, 1.0);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return Vec3.ZERO;
    }
}