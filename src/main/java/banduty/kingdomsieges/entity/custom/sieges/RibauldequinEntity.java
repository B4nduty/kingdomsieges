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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

    public RibauldequinEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.04)
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

        Item expected = getExpectedItem(stage);

        if (cooldown > 0) {
            return ActionResult.SUCCESS;
        }

        if (item != expected) {
            String expectedName = (expected != null) ? expected.getDefaultStack().getName().getString() : "Unknown";
            player.sendMessage(Text.translatable("entity." + Kingdomsieges.MOD_ID + ".ribauldequin_entity.next_load", expectedName), true);
            return ActionResult.SUCCESS;
        }

        if (!player.isCreative() && !consumeRequiredItems(player, stack, expected)) {
            return ActionResult.FAIL;
        }

        if (stage == 19) {
            triggerAnim("anim_controller", "loaded");
        }

        if (stage == 20 && item == Items.FLINT_AND_STEEL) {
            fireCannon();
            this.setOwner(player);
        }

        setLoadStage(stage + 1);

        return ActionResult.SUCCESS;
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

    private boolean consumeRequiredItems(PlayerEntity player, ItemStack stack, Item expected) {
        if (expected == SCItems.BLACK_POWDER.get()) {
            if (stack.getCount() < 2) {
                player.sendMessage(Text.translatable("entity." + Kingdomsieges.MOD_ID + ".ribauldequin_entity.black_powder_needed"), true);
                return false;
            }
            stack.decrement(2);
        } else if (expected == Items.IRON_NUGGET) {
            stack.decrement(1);
        } else if (expected == Items.FLINT_AND_STEEL || expected == KSItems.RAMROD.get()) {
            stack.damage(1, player.getRandom(), (ServerPlayerEntity) player);
            if (stack.getDamage() >= stack.getMaxDamage()) {
                stack.setCount(0);
            }
        }
        return true;
    }


    private void fireCannon() {
        triggerAnim("anim_controller", "fire");
        this.shotsRemaining = 5;
        this.nextShotTick = 0;

        setCooldown(90);
    }

    private void fireSingleShot(ServerWorld world, float angleOffset) {
        CannonProjectile projectile = new CannonProjectile(ModEntities.CANNON_BALL.get(), this, world);

        float baseYaw = this.getBodyYaw();
        float accuracyDegrees = 1.3f;
        float yawOffset = baseYaw + angleOffset + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float pitchOffset = this.getPitch() + (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;

        float yawRad = (float) Math.toRadians(yawOffset);
        float pitchRad = (float) Math.toRadians(pitchOffset);

        double xDir = -Math.sin(yawRad) * Math.cos(pitchRad);
        double yDir = -Math.sin(pitchRad);
        double zDir = Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3d direction = new Vec3d(xDir, yDir, zDir).normalize();

        double blocksPerSecond = 150.0;
        double blocksPerTick = blocksPerSecond / 20.0;

        float totalYaw = (float) Math.toRadians(this.getBodyYaw() + angleOffset);

        double forward = 1.7;
        double x = this.getX() - Math.sin(totalYaw) * forward;
        double y = this.getY() + 1.25f - Math.sin(Math.toRadians(this.getPitch()));
        double z = this.getZ() + Math.cos(totalYaw) * forward;

        projectile.setPos(x, y, z);
        Vec3d velocity = direction.multiply(blocksPerTick);
        projectile.setVelocity(velocity);

        projectile.setDamage(Kingdomsieges.getConfig().siegeEnginesOptions.ribauldequinBaseDamage());
        projectile.setDamageType(SCDamageCalculator.DamageType.BLUDGEONING);
        projectile.setOwner(this);
        projectile.setShouldBreakBlocks(false);

        world.spawnEntity(projectile);

        ((ServerWorld) this.getWorld()).spawnParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                20,      // count
                0.05, 0.05, 0.05, // offset
                0.01    // speed
        );

        world.getPlayers().forEach(p -> {
            double distance = p.getPos().distanceTo(this.getPos());
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
                    p.playSound(ModSounds.CANNON_CLOSE.get(), SoundCategory.AMBIENT, closeVolume, 1.0f);
                }
                if (distantVolume > 0f) {
                    p.playSound(ModSounds.CANNON_DISTANT.get(), SoundCategory.AMBIENT, distantVolume, 1.0f);
                }
            }
        });
        setLoadStage(0);

        Vec3d mouthPos = new Vec3d(x, y, z);
        spawnParticleTrail(world, velocity.normalize(), mouthPos, ModParticles.MUZZLES_SMOKE_PARTICLE.get(), 20, 0.2f, 0.0005f, 5);
        spawnParticleTrail(world, velocity.normalize(), mouthPos, ModParticles.MUZZLES_FLASH_PARTICLE.get(), 1, 0f, 0.1f, 6);
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
            if (this.moveTick >= 150 || this.moveTick == 0) {
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

        if (getLoadStage() != 19 && getLoadStage() != 20 && getCooldown() <= 0) {
            triggerAnim("anim_controller", "unloaded");
        }
        setCooldown(Math.max(0, getCooldown() - 1));

        if (shotsRemaining > 0) {
            if (nextShotTick <= 0) {
                fireSingleShot((ServerWorld) this.getWorld(), yawOffsets[5 - shotsRemaining]);

                shotsRemaining--;
                nextShotTick = 2;
                if (shotsRemaining == 0) setCooldown(0);
            } else {
                nextShotTick--;
            }
        }
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
            return 0.08d;
        }
        return 0.04d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, 0.0, 0.0);
    }
}