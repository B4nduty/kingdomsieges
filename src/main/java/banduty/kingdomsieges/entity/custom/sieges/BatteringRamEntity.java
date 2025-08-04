package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import net.minecraft.block.BlockState;
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
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

public class BatteringRamEntity extends AbstractSiegeEntity implements GeoEntity {
    private int moveTick;
    private final Random random = new Random();
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation attack = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);
    protected static final TrackedData<Boolean> ATTACK_HAPPENED;

    static {
        ATTACK_HAPPENED = DataTracker.registerData(BatteringRamEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public BatteringRamEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.025)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_HAPPENED, true);
    }

    public void setAttackHappened(boolean attackHappened) {
        this.dataTracker.set(ATTACK_HAPPENED, attackHappened);
    }

    public boolean getAttackHappened() {
        return this.dataTracker.get(ATTACK_HAPPENED);
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return this.getPassengerList().isEmpty() && (entity instanceof PlayerEntity || entity instanceof HorseEntity);
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
        if (this.getCooldown() <= 0 && player.isSneaking()) {
            this.triggerAnim("anim_controller", "attack");
            this.setAttackHappened(false);
            this.setCooldown(50);
            this.setOwner(this);

            serverWorld.getPlayers().forEach(p -> {
                double distance = p.getPos().distanceTo(this.getPos());

                if (distance <= 15) {
                    float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                    float volume = 1.0f - t;            // fades out

                    if (volume > 0f) {
                        p.playSound(ModSounds.ROPE_CHARGE_BR.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("attack", attack));

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

        boolean isMoving = this.getVelocity().x != 0 || this.getVelocity().z != 0;

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

        this.setCooldown(this.getCooldown() - 1);

        cleanInvalidBlockDamages();

        if (this.getCooldown() <= 6 && !this.getAttackHappened()) {
            // Destroy blocks in front
            destroyBlocksInFront();

            this.setAttackHappened(true);

            serverWorld.getPlayers().forEach(p -> {
                double distance = p.getPos().distanceTo(this.getPos());

                if (distance <= 40) {
                    float volume;

                    if (distance <= 5) {
                        volume = 1.0f;
                    } else {
                        float t = (float)((distance - 5) / 35.0); // normalize 6–40 -> 0–1
                        volume = 1.0f - t;                     // fades out
                    }

                    if (volume > 0f) {
                        p.playSound(ModSounds.RAM_IMPACT.get(), SoundCategory.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
        }
    }

    private void destroyBlocksInFront() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        Vec3d lookVec = this.getRotationVec(1.0F).normalize();
        Vec3d front = this.getPos().add(lookVec.multiply(2.5));

        int baseX = (int) Math.floor(front.x);
        int baseY = (int) Math.floor(this.getY() + 1);
        int baseZ = (int) Math.floor(front.z);

        double radius = 1.5;
        Vec3d boxCenter = new Vec3d(baseX + 0.5, baseY, baseZ + 0.5);
        Vec3d knockbackDir = this.getRotationVec(1.0F).normalize().multiply(2.5);

        serverWorld.getOtherEntities(this,
                new Box(
                        boxCenter.x - radius, boxCenter.y - 1, boxCenter.z - radius,
                        boxCenter.x + radius, boxCenter.y + 2, boxCenter.z + radius
                ),
                entity -> entity instanceof LivingEntity && !(entity instanceof PlayerEntity playerEntity && playerEntity.isCreative())
        ).forEach(entity -> {
            entity.damage(serverWorld.getDamageSources().mobAttack((LivingEntity) this.getOwner()), 18.0f);
            Vec3d knockback = new Vec3d(knockbackDir.x, 0.25, knockbackDir.z);
            entity.addVelocity(knockback);
            entity.velocityModified = true;
        });

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                    BlockState state = serverWorld.getBlockState(pos);

                    if (state.isAir() || state.getHardness(serverWorld, pos) < 0) continue;

                    float hardness = state.getHardness(serverWorld, pos);
                    float previousDamage = blockDamageMap.getOrDefault(pos, 0.0f);

                    float newDamage = previousDamage + (1.0f / (hardness * (60/18.0f)));

                    if (newDamage >= 1.0f) {
                        serverWorld.breakBlock(pos, true);
                        blockDamageMap.remove(pos);
                        clearBlockBreakingProgress(serverWorld, pos);
                    } else {
                        blockDamageMap.put(pos, newDamage);
                        sendBlockBreakingProgress(serverWorld, pos, newDamage);
                    }
                }
            }
        }
    }


    private void cleanInvalidBlockDamages() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        blockDamageMap.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            BlockState state = serverWorld.getBlockState(pos);

            // If block is air (i.e., broken), clean it up
            if (state.isAir()) {
                clearBlockBreakingProgress(serverWorld, pos);
                return true;
            }
            return false;
        });
    }

    private void sendBlockBreakingProgress(ServerWorld world, BlockPos pos, float progress) {
        int stage = (int)(progress * 10); // 0–9
        if (stage > 9) stage = 9;

        for (ServerPlayerEntity player : world.getPlayers()) {
            int visualId = getBlockVisualId(pos);
            player.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(visualId, pos, stage));
        }
    }

    private void clearBlockBreakingProgress(ServerWorld world, BlockPos pos) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            int visualId = getBlockVisualId(pos);
            player.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(visualId, pos, -1));
        }
    }

    private int getBlockVisualId(BlockPos pos) {
        // Offset the ID with hash to make it unique for each block
        return this.getId() ^ Long.hashCode(pos.asLong());
    }

    @Override
    public Vec3d getPassengerOffset(Entity entity) {
        if (entity instanceof HorseEntity) {
            return new Vec3d(0.0, 0.0, -1.25); // Left, Up, Back
        }
        return new Vec3d(0.0, 0.0, 1.25);
    }

    @Override
    public double getVelocity(Entity entity) {
        if (entity instanceof HorseEntity) {
            return 0.07d;
        }
        return 0.025d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, -0.7f, 0.0);
    }
}