package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.StoneyCore;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.particle.ModParticles;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.SCDamageCalculator;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CannonEntity extends AbstractSiegeEntity implements GeoEntity {
    private int moveTick;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation fire = RawAnimation.begin().then("fire", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loaded = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloaded = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    public int loadStage;

    public CannonEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.05)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.loadStage = nbt.getInt("loadStage");
        super.readNbt(nbt);
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return this.getPassengerList().isEmpty() && (entity instanceof PlayerEntity || entity instanceof HorseEntity);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("loadStage", loadStage);
        return super.writeNbt(nbt);
    }

    public int getLoadStage() {
        return loadStage;
    }

    public void setLoadStage(int loadStage) {
        this.loadStage = loadStage;
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
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        int stage = getLoadStage();
        int cooldown = getCooldown();

        Item expected = switch (stage) {
            case 0 -> SCItems.BLACK_POWDER.get();
            case 1, 3 -> KSItems.RAMROD.get();
            case 2 -> Items.STONE;
            case 4 -> Items.FLINT_AND_STEEL;
            default -> null;
        };

        if (cooldown > 0) {
            return ActionResult.SUCCESS;
        }

        if (item != expected) {
            String expectedName = (expected != null) ? expected.getDefaultStack().getName().getString() : "Unknown";
            player.sendMessage(Text.translatable("entity." + Kingdomsieges.MOD_ID + ".cannon_entity.next_load", expectedName), true);
            return ActionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            if (expected == SCItems.BLACK_POWDER.get()) {
                if (stack.getCount() < 32) {
                    player.sendMessage(Text.translatable("entity." + Kingdomsieges.MOD_ID + ".cannon_entity.black_powder_needed"), true);
                    return ActionResult.FAIL;
                }
                stack.decrement(32);
            } else if (expected == Items.STONE) {
                stack.decrement(1);
            } else if (expected == Items.FLINT_AND_STEEL || expected == KSItems.RAMROD.get()) {
                stack.damage(1, player.getRandom(), (ServerPlayerEntity) player);
                if (stack.getDamage() >= stack.getMaxDamage()) {
                    stack.setCount(0);
                }
            }
        }

        setLoadStage(stage + 1);

        if (getLoadStage() == 4) {
            triggerAnim("anim_controller", "loaded");
        }

        if (getLoadStage() == 5 && getFirstPassenger() == null) {
            this.setOwner(player);
            fireCannon(serverWorld);
        }

        return ActionResult.SUCCESS;
    }

    private void fireCannon(ServerWorld serverWorld) {
        triggerAnim("anim_controller", "fire");
        CannonProjectile projectile = new CannonProjectile(ModEntities.CANNON_BALL.get(), this, serverWorld);

        Vec3d mouthPos = getMouthOffset();

        projectile.setPos(mouthPos.x, mouthPos.y, mouthPos.z);

        double blocksPerSecond = 140.0;
        double blocksPerTick = blocksPerSecond / 20.0;
        float accuracyDegrees = 1.2f;
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float pitchOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = this.getBodyYaw() + yawOffset;
        float adjustedPitch = this.getPitch() + pitchOffset;
        float yawRad = adjustedYaw * (float) (Math.PI / 180.0);
        float pitchRad = adjustedPitch * (float) (Math.PI / 180.0);
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3d direction = new Vec3d(x, y, z).normalize();
        Vec3d velocity = direction.multiply(blocksPerTick);
        projectile.setVelocity(velocity);

        projectile.setDamage(Kingdomsieges.getConfig().siegeEnginesOptions.cannonBaseDamage());
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);

        serverWorld.spawnEntity(projectile);

        serverWorld.getPlayers().forEach(p -> {
            double distance = p.getPos().distanceTo(this.getPos());

            if (distance <= 200) {
                float closeVolume;
                float distantVolume = 0f;

                if (distance <= 50) {
                    closeVolume = 1.0f;
                } else {
                    float t = (float)((distance - 50) / 150.0); // normalize 51–200 -> 0–1
                    closeVolume = 1.0f - t;                     // fades out
                    distantVolume = t;                          // fades in
                }

                if (closeVolume > 0f) {
                    p.playSound(ModSounds.CANNON_CLOSE.get(), SoundCategory.AMBIENT, closeVolume, 1.0f);
                }

                if (distantVolume > 0f) {
                    p.playSound(ModSounds.CANNON_DISTANT.get(), SoundCategory.AMBIENT, distantVolume, 1.0f);
                }
            }
        });

        setLoadStage(0);
        setCooldown(90);

        ((ServerWorld) this.getWorld()).spawnParticles(
                ParticleTypes.SMOKE,
                mouthPos.x, mouthPos.y, mouthPos.z,
                20,      // count
                0.05, 0.05, 0.05, // offset
                0.01    // speed
        );

        spawnParticleTrail(serverWorld, velocity.normalize(), mouthPos, ModParticles.MUZZLES_SMOKE_PARTICLE.get(), 100, 0.2f, 0.0005f, 5);
        spawnParticleTrail(serverWorld, velocity.normalize(), mouthPos, ModParticles.MUZZLES_FLASH_PARTICLE.get(), 1, 0f, 0.1f, 6);
    }

    private static void spawnParticleTrail(ServerWorld world, Vec3d direction, Vec3d pos, ParticleEffect particle, int count, float delta, float spread, int distance) {
        List<Vec3d> trailPositions = new ArrayList<>();
        for (int i = 0; i < distance; i++) {
            trailPositions.add(pos.add(direction.multiply(i)));
        }

        for (Vec3d blockPos : trailPositions) {
            world.spawnParticles(particle, blockPos.x, blockPos.y, blockPos.z, count, delta, delta, delta, spread);
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

        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        boolean isMoving = this.getVelocity().x != 0 || this.getVelocity().y != 0;

        if (isMoving && isAlive()) {
            if (this.moveTick >= 140 || this.moveTick == 0) {
                serverWorld.getPlayers().forEach(p -> {
                    if (p.getPos().distanceTo(this.getPos()) <= 30) {
                        p.playSound(ModSounds.SIEGE_ENGINE_MOVE.get(), SoundCategory.AMBIENT, (float) (1.0f - p.getPos().distanceTo(this.getPos()) / 30), 1.0f);
                    }
                });
                if (this.moveTick != 0) this.moveTick = 0;
            }
            moveTick++;
        } else if (this.moveTick != 0 || !isAlive()) {
            this.moveTick = 0;
            serverWorld.getPlayers().forEach(p -> {
                p.networkHandler.sendPacket(new StopSoundS2CPacket(ModSounds.SIEGE_ENGINE_MOVE.get().getId(), SoundCategory.AMBIENT));
            });
        }

        if (getLoadStage() != 4 && getLoadStage() != 5 && getCooldown() <= 10) {
            triggerAnim("anim_controller", "unloaded");
        }
        setCooldown(Math.max(0, getCooldown() - 1));
    }

    private Vec3d getMouthOffset() {
        float yawRad = (float) Math.toRadians(this.getBodyYaw());

        double forward = 1.4;
        double up = 1;
        double x = this.getX() - Math.sin(yawRad) * forward;
        double y = this.getY() + up - Math.sin(Math.toRadians(this.getPitch()));
        StoneyCore.LOGGER.info("Y pos: " + y);
        double z = this.getZ() + Math.cos(yawRad) * forward;

        return new Vec3d(x, y, z);
    }

    @Override
    public Vec3d getPassengerOffset(Entity entity) {
        if (entity instanceof HorseEntity) {
            return new Vec3d(0.0, 0.0, -1.5); // Left, Up, Back
        }
        return new Vec3d(0.5, 0.0, 1.0);
    }

    @Override
    public double getVelocity(Entity entity) {
        if (entity instanceof HorseEntity) {
            return 0.1d;
        }
        return 0.05d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, 0.0, 0.0);
    }
}