package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.SCDamageCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
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

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class MangonelEntity extends AbstractSiegeEntity implements GeoEntity {
    private final Random random = new Random();
    private int moveTick;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation shoot = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation reloading = RawAnimation.begin().then("reloading", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loaded = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloaded = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);
    protected static final TrackedData<String> AMMO_LOADED;

    static {
        AMMO_LOADED = DataTracker.registerData(MangonelEntity.class, TrackedDataHandlerRegistry.STRING);
    }

    public int reloadingTime;

    public MangonelEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.075)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AMMO_LOADED, "");
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return this.getPassengerList().isEmpty() && (entity instanceof PlayerEntity || entity instanceof HorseEntity);
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

        super.interact(player, hand);

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

        // Passenger check
        if (this.getFirstPassenger() != null) return ActionResult.FAIL;

        // Interaction logic
        int cooldown = getCooldown();
        ItemStack itemStack = player.getStackInHand(hand);
        if (cooldown > 0) return ActionResult.FAIL;

        if (getAmmoLoaded() == null || getAmmoLoaded().isEmpty()) {
            if (itemStack.isOf(Items.STONE)) {
                itemStack.decrement(1);
                setAmmoLoaded("stone");
                setReloadingTime(100);
                triggerAnim("anim_controller", "reloading");
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
                setReloadingTime(100);
                triggerAnim("anim_controller", "reloading");
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
                player.sendMessage(Text.translatable("entity.kingdomsieges.mangonel_entity.ammo_needed"), true);
                return ActionResult.FAIL;
            }
        }

        if (getReloadingTime() == 0) {
            triggerAnim("anim_controller", "shoot");
            setCooldown(10);
            serverWorld.getPlayers().forEach(p -> {
                double distance = p.getPos().distanceTo(this.getPos());

                if (distance <= 75) {
                    float t = (float)(distance / 75.0); // normalize 0–15 -> 0–1
                    float volume = 1.0f - t;            // fades out

                    if (volume > 0f) {
                        p.playSound(ModSounds.MANGONEL_SHOOT.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
            this.setOwner(player);
        }

        return ActionResult.SUCCESS;
    }

    private void fireMangonel(ServerWorld serverWorld) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(ModEntities.TREBUCHET_PROJECTILE.get(), this, serverWorld);

        projectile.setPos(this.getX(), this.getY() + 2.25d, this.getZ());

        double blocksPerSecond = 80.0;
        double blocksPerTick = blocksPerSecond / 20.0;
        float accuracyDegrees = 1f;
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = this.getBodyYaw() + yawOffset;
        float yawRad = adjustedYaw * (float) (Math.PI / 180.0);
        double x = -Math.sin(yawRad);
        double z = Math.cos(yawRad);
        Vec3d direction = new Vec3d(x, 0, z).normalize();
        projectile.setVelocity(direction.multiply(blocksPerTick));

        float damage = 27.0F;
        projectile.setDamage(damage);
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);

        if (getAmmoLoaded().equals("stone")) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.BREAK_BLOCKS);
            projectile.setTextureName("stone");
        } else if (getAmmoLoaded().equals("magma")) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_FIRE);
            projectile.setTextureName("magma");
        }

        serverWorld.spawnEntity(projectile);

        setAmmoLoaded("");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("shoot", shoot)
                .triggerableAnim("reloading", reloading)
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

        boolean isMoving = this.getVelocity().x != 0 || this.getVelocity().y != 0;

        if (isMoving) {
            if (this.moveTick >= 150 || this.moveTick == 0) {
                serverWorld.getPlayers().forEach(p -> {
                    if (p.getPos().distanceTo(this.getPos()) <= 30) {
                        p.playSound(ModSounds.SIEGE_ENGINE_MOVE.get(), SoundCategory.AMBIENT, (float) (1.0f - p.getPos().distanceTo(this.getPos()) / 30), 1.0f);
                    }
                });
                if (this.moveTick != 0) this.moveTick = 0;
            }
            moveTick++;
        } else if (this.moveTick != 0) {
            this.moveTick = 0;
            serverWorld.getPlayers().forEach(p -> {
                p.networkHandler.sendPacket(new StopSoundS2CPacket(ModSounds.SIEGE_ENGINE_MOVE.get().getId(), SoundCategory.AMBIENT));
            });
        }

        if (getAmmoLoaded() != null && !getAmmoLoaded().isEmpty()) {
            if (getReloadingTime() <= 0 && getCooldown() <= 0) triggerAnim("anim_controller", "loaded");
            if (getCooldown() == 7) fireMangonel(serverWorld);
        } else if (getCooldown() <= 0 && getReloadingTime() <= 0) triggerAnim("anim_controller", "unloaded");

        setReloadingTime(Math.max(0, getReloadingTime() - 1));
        setCooldown(Math.max(0, getCooldown() - 1));
    }

    @Override
    public Vec3d getPassengerOffset(Entity entity) {
        if (entity instanceof HorseEntity) {
            return new Vec3d(0.0, 0.0, -1.25); // Left, Up, Back
        }
        return new Vec3d(0.0, 0.0, 2.0);
    }

    @Override
    public double getVelocity(Entity entity) {
        if (entity instanceof HorseEntity) {
            return 0.1d;
        }
        return 0.06d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, -0.7f, 0.0);
    }
}