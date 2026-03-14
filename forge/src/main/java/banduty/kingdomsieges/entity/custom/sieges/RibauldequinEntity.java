package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.particle.ModParticles;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.SCDamageCalculator;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RibauldequinEntity extends AbstractSiegeEntity implements GeoEntity {
    private int moveTick;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation fire = RawAnimation.begin().then("ribauldfire", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loaded = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloaded = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);
    private int nextShotTick = -1;
    private int shotsRemaining = 0;
    private final float[] yawOffsets = {-45.0f, -22.5f, 0f, 22.5f, 45.0f};

    public int loadStage;

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
    public void load(CompoundTag nbt) {
        this.loadStage = nbt.getInt("loadStage");
        super.load(nbt);
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return this.getPassengers().isEmpty() && (entity instanceof Player || entity instanceof Horse);
    }

    @Override
    public CompoundTag saveWithoutId(CompoundTag nbt) {
        nbt.putInt("loadStage", loadStage);
        return super.saveWithoutId(nbt);
    }

    public int getLoadStage() {
        return loadStage;
    }

    public void setLoadStage(int loadStage) {
        this.loadStage = loadStage;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!(this.level() instanceof ServerLevel serverLevel) || hand != InteractionHand.MAIN_HAND) {
            return super.interact(player, hand);
        }

        super.interact(player, hand);

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

        // Passenger check
        if (this.getFirstPassenger() != null) return InteractionResult.FAIL;

        // Interaction logic
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        int stage = getLoadStage();
        int cooldown = getCooldown();

        Item expected = getExpectedItem(stage);

        if (cooldown > 0) {
            return InteractionResult.SUCCESS;
        }

        if (item != expected) {
            String expectedName = (expected != null) ? expected.getDefaultInstance().getHoverName().getString() : "Unknown";
            player.displayClientMessage(Component.translatable("entity." + Kingdomsieges.MOD_ID + ".ribauldequin_entity.next_load", expectedName), true);
            return InteractionResult.SUCCESS;
        }

        if (!player.isCreative() && !consumeRequiredItems(player, stack, expected)) {
            return InteractionResult.FAIL;
        }

        if (stage == 19) {
            triggerAnim("anim_controller", "loaded");
        }

        if (stage == 20 && item == Items.FLINT_AND_STEEL) {
            fireCannon();
            this.setOwner(player);
        }

        setLoadStage(stage + 1);

        return InteractionResult.SUCCESS;
    }

    private Item getExpectedItem(int stage) {
        if (stage == 20) return Items.FLINT_AND_STEEL;

        int cycle = stage % 4;
        return switch (cycle) {
            case 0 -> SCItems.BLACK_POWDER.get();
            case 1, 3 -> KSItems.RAMROD.get();
            case 2 -> Items.IRON_NUGGET;
            default -> null;
        };
    }

    private boolean consumeRequiredItems(Player player, ItemStack stack, Item expected) {
        if (expected == SCItems.BLACK_POWDER.get()) {
            if (stack.getCount() < 2) {
                player.displayClientMessage(Component.translatable("entity." + Kingdomsieges.MOD_ID + ".ribauldequin_entity.black_powder_needed"), true);
                return false;
            }
            stack.shrink(2);
        } else if (expected == Items.IRON_NUGGET) {
            stack.shrink(1);
        } else if (expected == Items.FLINT_AND_STEEL || expected == KSItems.RAMROD.get()) {
            stack.hurtAndBreak(1, player, (playerT) -> playerT.broadcastBreakEvent(player.getUsedItemHand()));
        }
        return true;
    }


    private void fireCannon() {
        triggerAnim("anim_controller", "fire");
        this.shotsRemaining = 5;
        this.nextShotTick = 0;

        setCooldown(90);
    }

    private void fireSingleShot(ServerLevel serverLevel, float angleOffset) {
        CannonProjectile projectile = new CannonProjectile(ModEntities.CANNON_BALL.get(), this, serverLevel);

        float baseYaw = this.getVisualRotationYInDegrees();
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = baseYaw + angleOffset + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float pitchOffset = this.getXRot() + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;

        float yawRad = (float) Math.toRadians(yawOffset);
        float pitchRad = (float) Math.toRadians(pitchOffset);

        double xDir = -Math.sin(yawRad) * Math.cos(pitchRad);
        double yDir = -Math.sin(pitchRad);
        double zDir = Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3 direction = new Vec3(xDir, yDir, zDir).normalize();

        double blocksPerTick = getProjectileSpeed() / 20.0;

        float totalYaw = (float) Math.toRadians(this.getVisualRotationYInDegrees() + angleOffset);

        double forward = 1.7;
        double x = this.getX() - Math.sin(totalYaw) * forward;
        double y = this.getY() + 1.25f - Math.sin(Math.toRadians(this.getXRot()));
        double z = this.getZ() + Math.cos(totalYaw) * forward;

        projectile.setPos(x, y, z);
        Vec3 velocity = direction.scale(blocksPerTick);
        projectile.setDeltaMovement(velocity);

        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);
        projectile.setShouldBreakBlocks(false);

        serverLevel.addFreshEntity(projectile);

        serverLevel.sendParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                20,      // count
                0.05, 0.05, 0.05, // offset
                0.01    // speed
        );

        serverLevel.players().forEach(p -> {
            double distance = p.position().distanceTo(this.position());
            if (distance <= 200) {
                float closeVolume;
                float distantVolume = 0f;

                if (distance <= 50) {
                    closeVolume = 1.0f;
                } else {
                    float t = (float) ((distance - 50) / 150.0);
                    closeVolume = 1.0f - t;
                    distantVolume = t;
                }

                if (closeVolume > 0f) {
                    p.playNotifySound(ModSounds.CANNON_CLOSE.get(), SoundSource.AMBIENT, closeVolume, 1.0f);
                }
                if (distantVolume > 0f) {
                    p.playNotifySound(ModSounds.CANNON_DISTANT.get(), SoundSource.AMBIENT, distantVolume, 1.0f);
                }
            }
        });
        setLoadStage(0);

        Vec3 mouthPos = new Vec3(x, y, z);
        spawnParticleTrail(serverLevel, velocity.normalize(), mouthPos, ModParticles.MUZZLES_SMOKE_PARTICLE.get(), 20, 0.2f, 0.0005f, 5);
        spawnParticleTrail(serverLevel, velocity.normalize(), mouthPos, ModParticles.MUZZLES_FLASH_PARTICLE.get(), 1, 0f, 0.1f, 6);
    }

    private static void spawnParticleTrail(ServerLevel serverLevel, Vec3 direction, Vec3 pos, ParticleOptions particleOptions, int count, float delta, float spread, int distance) {
        List<Vec3> trailPositions = new ArrayList<>();
        for (int i = 0; i < distance; i++) {
            trailPositions.add(pos.add(direction.scale(i)));
        }

        for (Vec3 blockPos : trailPositions) {
            serverLevel.sendParticles(particleOptions, blockPos.x, blockPos.y, blockPos.z, count, delta, delta, delta, spread);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("fire", fire)
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

        boolean isMoving = this.getDeltaMovement().x != 0 || this.getDeltaMovement().y != 0;

        if (isMoving && isAlive()) {
            if (this.moveTick >= 150 || this.moveTick == 0) {
                serverLevel.players().forEach(p -> {
                    if (p.position().distanceTo(this.position()) <= 30) {
                        p.playNotifySound(ModSounds.SIEGE_ENGINE_MOVE.get(), SoundSource.AMBIENT, (float) (1.0f - p.position().distanceTo(this.position()) / 30), 1.0f);
                    }
                });
                if (this.moveTick != 0) this.moveTick = 0;
            }
            moveTick++;
        } else if (this.moveTick != 0 || !isAlive()) {
            this.moveTick = 0;
            serverLevel.players().forEach(p -> {
                p.connection.send(new ClientboundStopSoundPacket(ModSounds.SIEGE_ENGINE_MOVE.get().getLocation(), SoundSource.AMBIENT));
            });
        }

        if (getLoadStage() != 19 && getLoadStage() != 20 && getCooldown() <= 0) {
            triggerAnim("anim_controller", "unloaded");
        }
        setCooldown(Math.max(0, getCooldown() - 1));

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

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        if (entity instanceof Horse) {
            return new Vec3(0.0, 0.0, -1.5); // Left, Up, Back
        }
        return new Vec3(0.5, 0.0, 1.0);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, 0.0, 0.0);
    }
}