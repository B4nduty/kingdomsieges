package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.networking.ModMessages;
import banduty.stoneycore.networking.packet.SiegeYawS2CPacket;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.SCDamageCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class TrebuchetEntity extends AbstractSiegeEntity implements GeoEntity {
    private final Random random = new Random();
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation shoot = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation reload = RawAnimation.begin().then("reload", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loaded = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloaded = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);
    protected static final EntityDataAccessor<String> AMMO_LOADED;

    static {
        AMMO_LOADED = SynchedEntityData.defineId(TrebuchetEntity.class, EntityDataSerializers.STRING);
    }

    public int reloadingTime;

    public TrebuchetEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AMMO_LOADED, "");
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return false;
    }

    public String getAmmoLoaded() {
        return this.entityData.get(AMMO_LOADED);
    }

    public void setAmmoLoaded(String ammoLoaded) {
        this.entityData.set(AMMO_LOADED, ammoLoaded);
    }

    public int getReloadingTime() {
        return reloadingTime;
    }

    public void setReloadingTime(int reloadingTime) {
        this.reloadingTime = reloadingTime;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!(this.level() instanceof ServerLevel serverLevel) || hand != InteractionHand.MAIN_HAND) {
            return super.interact(player, hand);
        }

        UUID playerId = player.getUUID();

        // Siege check
        Optional<SiegeManager.Siege> siegeOpt = SiegeManager.getPlayerSiege(serverLevel, playerId);
        if (siegeOpt.map(siege -> siege.isDisabled(playerId)).orElse(false)) {
            return InteractionResult.FAIL;
        }

        // Land ownership check
        LandState stateManager = LandState.get(serverLevel);
        Optional<Land> maybeLand = stateManager.getLandAt(this.getOnPos());
        boolean isOwnerOrAlly = maybeLand
                .map(land -> land.getOwnerUUID().equals(playerId) || land.isAlly(playerId) || player.isCreative())
                .orElse(true);
        if (!isOwnerOrAlly) {
            return InteractionResult.FAIL;
        }

        // Interaction logic
        int cooldown = getCooldown();
        if (cooldown > 0) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) {
            this.setTrackedYaw(this.getTrackedYaw() + 45.0f);
            this.setYRot(this.getTrackedYaw());
            this.setYBodyRot(this.getTrackedYaw());
            this.setYHeadRot(this.getTrackedYaw());
            this.yBodyRotO = this.yBodyRot;
            this.yHeadRotO = this.yHeadRot;

            List<ServerPlayer> players = serverLevel.players();

            for(ServerPlayer playerEntity : players) {
                ModMessages.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> playerEntity),
                        new SiegeYawS2CPacket(this.getYRot(), this.getXRot(), this.getWheelRotation())
                );
            }

            return InteractionResult.SUCCESS;
        }
        ItemStack itemStack = player.getItemInHand(hand);

        if (getAmmoLoaded() == null || getAmmoLoaded().isEmpty()) {
            if (itemStack.is(Items.STONE)) {
                itemStack.shrink(1);
                setAmmoLoaded("stone");
                setReloadingTime(getBaseReload());
                triggerAnim("anim_controller", "reload");
                serverLevel.players().forEach(p -> {
                    double distance = p.position().distanceTo(this.position());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playNotifySound(ModSounds.ROPE_CHARGE_GN.get(), SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return InteractionResult.SUCCESS;
            } else if (itemStack.is(Items.MAGMA_BLOCK)) {
                itemStack.shrink(1);
                setAmmoLoaded("magma");
                setReloadingTime(getBaseReload());
                triggerAnim("anim_controller", "reload");
                serverLevel.players().forEach(p -> {
                    double distance = p.position().distanceTo(this.position());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playNotifySound(ModSounds.ROPE_CHARGE_GN.get(), SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return InteractionResult.SUCCESS;
            }  else if (itemStack.is(Items.ROTTEN_FLESH)) {
                if (itemStack.getCount() < 9) {
                    player.displayClientMessage(Component.translatable("entity.kingdomsieges.trebuchet_entity.rotten_flesh"), true);
                    return InteractionResult.FAIL;
                }
                itemStack.shrink(9);
                setAmmoLoaded("rotten_flesh");
                setReloadingTime(getBaseReload());
                triggerAnim("anim_controller", "reload");
                serverLevel.players().forEach(p -> {
                    double distance = p.position().distanceTo(this.position());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playNotifySound(ModSounds.ROPE_CHARGE_GN.get(), SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable("entity.kingdomsieges.trebuchet_entity.ammo_needed"), true);
                return InteractionResult.FAIL;
            }
        }

        if (getReloadingTime() == 0) {
            triggerAnim("anim_controller", "shoot");
            setCooldown(60);
            serverLevel.players().forEach(p -> {
                double distance = p.position().distanceTo(this.position());

                if (distance <= 75) {
                    float t = (float)(distance / 75.0); // normalize 0–15 -> 0–1
                    float volume = 1.0f - t;            // fades out

                    if (volume > 0f) {
                        p.playNotifySound(ModSounds.TREBUCHET_SHOOT.get(), SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
            this.setOwner(player);
        }

        return InteractionResult.SUCCESS;
    }

    private void fireTrebuchet(ServerLevel serverLevel) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(ModEntities.TREBUCHET_PROJECTILE.get(), this, serverLevel);

        projectile.setPos(this.getX(), this.getY() + 12d, this.getZ());

        double blocksPerTick = getProjectileSpeed() / 20.0;
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = this.getTrackedYaw() + yawOffset;
        float yawRad = adjustedYaw * (float) (Math.PI / 180.0);
        double x = -Math.sin(yawRad);
        double z = Math.cos(yawRad);
        Vec3 direction = new Vec3(x, 0, z).normalize();
        projectile.setDeltaMovement(direction.scale(blocksPerTick));

        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);

        if (getAmmoLoaded().equals("stone")) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.BREAK_BLOCKS);
            projectile.setTextureName("stone");
        } else if (getAmmoLoaded().equals("magma")) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_FIRE);
            projectile.setTextureName("magma");
        } else if (getAmmoLoaded().equals("rotten_flesh")) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_EFFECT);
            projectile.setStatusEffectInstance(new MobEffectInstance(MobEffects.POISON, 100, 0, true, true, true));
            projectile.setCloudDuration(600);
            projectile.setTextureName("rotten_flesh");
        }

        serverLevel.addFreshEntity(projectile);

        setAmmoLoaded("");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("shoot", shoot)
                .triggerableAnim("reload", reload).setAnimationSpeed(getReloadingTime() > 0 ? 200d / getReloadingTime() : 1.0)
                .triggerableAnim("loaded", loaded)
                .triggerableAnim("unloaded", unloaded));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (getAmmoLoaded() != null && !getAmmoLoaded().isEmpty()) {
            if (getReloadingTime() <= 0 && getCooldown() <= 0) {
                triggerAnim("anim_controller", "loaded");
            }
            if (getCooldown() == 52) fireTrebuchet(serverLevel);
        } else if (getCooldown() <= 0 && getReloadingTime() <= 0) triggerAnim("anim_controller", "unloaded");

        setReloadingTime(Math.max(0, getReloadingTime() - 1));
        setCooldown(Math.max(0, getCooldown() - 1));
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return new Vec3(0.0, 0.0, 0.0);
    }

    @Override
    public double getVelocity(Entity entity) {
        return 0d;
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, 0.0f, 0.0);
    }
}