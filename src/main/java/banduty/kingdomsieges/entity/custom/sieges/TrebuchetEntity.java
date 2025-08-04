package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.networking.ModMessages;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.SCDamageCalculator;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    protected static final TrackedData<String> AMMO_LOADED;

    static {
        AMMO_LOADED = DataTracker.registerData(TrebuchetEntity.class, TrackedDataHandlerRegistry.STRING);
    }

    public int reloadingTime;

    public TrebuchetEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AMMO_LOADED, "");
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return false;
    }

    public String getAmmoLoaded() {
        return this.dataTracker.get(AMMO_LOADED);
    }

    public void setAmmoLoaded(String ammoLoaded) {
        this.dataTracker.set(AMMO_LOADED, ammoLoaded);
    }

    public int getReloadingTime() {
        return reloadingTime;
    }

    public void setReloadingTime(int reloadingTime) {
        this.reloadingTime = reloadingTime;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld) || hand != Hand.MAIN_HAND) {
            return super.interact(player, hand);
        }

        UUID playerId = player.getUuid();

        // Siege check
        Optional<SiegeManager.Siege> siegeOpt = SiegeManager.getPlayerSiege(serverWorld, playerId);
        if (siegeOpt.map(siege -> siege.isDisabled(playerId)).orElse(false)) {
            return ActionResult.FAIL;
        }

        // Land ownership check
        LandState stateManager = LandState.get(serverWorld);
        Optional<Land> maybeLand = stateManager.getLandAt(this.getBlockPos());
        boolean isOwnerOrAlly = maybeLand
                .map(land -> land.getOwnerUUID().equals(playerId) || land.isAlly(playerId) || player.isCreative())
                .orElse(true);
        if (!isOwnerOrAlly) {
            return ActionResult.FAIL;
        }

        // Interaction logic
        int cooldown = getCooldown();
        if (cooldown > 0) return ActionResult.FAIL;

        if (player.isSneaking()) {
            this.setTrackedYaw(this.getTrackedYaw() + 45.0f);
            this.setYaw(this.getTrackedYaw());
            this.setBodyYaw(this.getTrackedYaw());
            this.setHeadYaw(this.getTrackedYaw());
            this.prevBodyYaw = this.bodyYaw;
            this.prevHeadYaw = this.headYaw;

            List<ServerPlayerEntity> players = serverWorld.getPlayers();

            for(ServerPlayerEntity playerEntity : players) {
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeFloat(this.getYaw());
                buffer.writeFloat(this.getPitch());
                buffer.writeFloat(this.getWheelRotation());
                ServerPlayNetworking.send(playerEntity, ModMessages.SIEGE_YAW_PITCH_S2C_ID, buffer);
            }

            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = player.getStackInHand(hand);

        if (getAmmoLoaded() == null || getAmmoLoaded().isEmpty()) {
            if (itemStack.isOf(Items.STONE)) {
                itemStack.decrement(1);
                setAmmoLoaded("stone");
                setReloadingTime(200);
                triggerAnim("anim_controller", "reload");
                serverWorld.getPlayers().forEach(p -> {
                    double distance = p.getPos().distanceTo(this.getPos());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playSound(ModSounds.ROPE_CHARGE_GN.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return ActionResult.SUCCESS;
            } else if (itemStack.isOf(Items.MAGMA_BLOCK)) {
                itemStack.decrement(1);
                setAmmoLoaded("magma");
                setReloadingTime(200);
                triggerAnim("anim_controller", "reload");
                serverWorld.getPlayers().forEach(p -> {
                    double distance = p.getPos().distanceTo(this.getPos());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playSound(ModSounds.ROPE_CHARGE_GN.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return ActionResult.SUCCESS;
            }  else if (itemStack.isOf(Items.ROTTEN_FLESH)) {
                if (itemStack.getCount() < 9) {
                    player.sendMessage(Text.translatable("entity.kingdomsieges.trebuchet_entity.rotten_flesh"), true);
                    return ActionResult.FAIL;
                }
                itemStack.decrement(9);
                setAmmoLoaded("rotten_flesh");
                setReloadingTime(200);
                triggerAnim("anim_controller", "reload");
                serverWorld.getPlayers().forEach(p -> {
                    double distance = p.getPos().distanceTo(this.getPos());

                    if (distance <= 15) {
                        float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                        float volume = 1.0f - t;            // fades out

                        if (volume > 0f) {
                            p.playSound(ModSounds.ROPE_CHARGE_GN.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                        }
                    }
                });
                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.translatable("entity.kingdomsieges.trebuchet_entity.ammo_needed"), true);
                return ActionResult.FAIL;
            }
        }

        if (getReloadingTime() == 0) {
            triggerAnim("anim_controller", "shoot");
            setCooldown(60);
            serverWorld.getPlayers().forEach(p -> {
                double distance = p.getPos().distanceTo(this.getPos());

                if (distance <= 75) {
                    float t = (float)(distance / 75.0); // normalize 0–15 -> 0–1
                    float volume = 1.0f - t;            // fades out

                    if (volume > 0f) {
                        p.playSound(ModSounds.TREBUCHET_SHOOT.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
            this.setOwner(player);
        }

        return ActionResult.SUCCESS;
    }

    private void fireTrebuchet(ServerWorld serverWorld) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(ModEntities.TREBUCHET_PROJECTILE.get(), this, serverWorld);

        projectile.setPos(this.getX(), this.getY() + 12d, this.getZ());

        double blocksPerSecond = 100.0;
        double blocksPerTick = blocksPerSecond / 20.0;
        float accuracyDegrees = 0.25f;
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = this.getBodyYaw() + yawOffset;
        float yawRad = adjustedYaw * (float) (Math.PI / 180.0);
        double x = -Math.sin(yawRad);
        double z = Math.cos(yawRad);
        Vec3d direction = new Vec3d(x, 0, z).normalize();
        projectile.setVelocity(direction.multiply(blocksPerTick));

        float damage = 42.0F;
        projectile.setDamage(damage);
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
            projectile.setStatusEffectInstance(new StatusEffectInstance(StatusEffects.POISON, 100, 0, true, true, true));
            projectile.setCloudDuration(600);
            projectile.setTextureName("rotten_flesh");
        }

        serverWorld.spawnEntity(projectile);

        setAmmoLoaded("");

//        serverWorld.getPlayers().forEach(p -> {
//            double distance = p.getPos().distanceTo(this.getPos());
//
//            if (distance <= 200) {
//                float closeVolume;
//                float distantVolume = 0f;
//
//                if (distance <= 50) {
//                    closeVolume = 1.0f;
//                } else {
//                    float t = (float)((distance - 50) / 150.0); // normalize 51–200 -> 0–1
//                    closeVolume = 1.0f - t;                     // fades out
//                    distantVolume = t;                          // fades in
//                }
//
//                if (closeVolume > 0f) {
//                    p.playSound(ModSounds.CANNON_CLOSE.get(), SoundCategory.AMBIENT, closeVolume, 1.0f);
//                }
//
//                if (distantVolume > 0f) {
//                    p.playSound(ModSounds.CANNON_DISTANT.get(), SoundCategory.AMBIENT, distantVolume, 1.0f);
//                }
//            }
//        });

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("shoot", shoot)
                .triggerableAnim("reload", reload)
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

        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        if (getAmmoLoaded() != null && !getAmmoLoaded().isEmpty()) {
            if (getReloadingTime() <= 0 && getCooldown() <= 0) triggerAnim("anim_controller", "loaded");
            if (getCooldown() == 52) fireTrebuchet(serverWorld);
        } else if (getCooldown() <= 0 && getReloadingTime() <= 0) triggerAnim("anim_controller", "unloaded");

        setReloadingTime(Math.max(0, getReloadingTime() - 1));
        setCooldown(Math.max(0, getCooldown() - 1));
    }

    @Override
    public Vec3d getPassengerOffset(Entity entity) {
        return new Vec3d(0.0, 0.0, 0.0);
    }

    @Override
    public double getVelocity(Entity entity) {
        return 0d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, 0.0f, 0.0);
    }
}