package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    protected static final EntityDataAccessor<Boolean> ATTACK_HAPPENED;

    static {
        ATTACK_HAPPENED = SynchedEntityData.defineId(BatteringRamEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public BatteringRamEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.MOVEMENT_SPEED, 0.025)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_HAPPENED, true);
    }

    public void setAttackHappened(boolean attackHappened) {
        this.entityData.set(ATTACK_HAPPENED, attackHappened);
    }

    public boolean getAttackHappened() {
        return this.entityData.get(ATTACK_HAPPENED);
    }

    @Override
    public boolean canAddPassenger(Entity entity) {
        return this.getPassengers().isEmpty() && (entity instanceof Player || entity instanceof Horse);
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
        if (this.getCooldown() <= 0 && player.isShiftKeyDown()) {
            this.triggerAnim("anim_controller", "attack");
            this.setAttackHappened(false);
            this.setCooldown(50);
            this.setOwner(this);

            serverLevel.players().forEach(p -> {
                double distance = p.position().distanceTo(this.position());

                if (distance <= 15) {
                    float t = (float)(distance / 15.0); // normalize 0–15 -> 0–1
                    float volume = 1.0f - t;            // fades out

                    if (volume > 0f) {
                        p.playNotifySound(ModSounds.ROPE_CHARGE_BR, SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
        }

        return InteractionResult.SUCCESS;
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

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean isMoving = this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0;

        if (isMoving && isAlive()) {
            if (this.moveTick >= 150 || this.moveTick == 0) {
                serverLevel.players().forEach(p -> {
                    if (p.position().distanceTo(this.position()) <= 30) {
                        p.playNotifySound(ModSounds.SIEGE_ENGINE_MOVE, SoundSource.AMBIENT, (float) (1.0f - p.position().distanceTo(this.position()) / 30), 1.0f);
                    }
                });
                if (this.moveTick != 0) this.moveTick = 0;
            }
            moveTick++;
        } else if (this.moveTick != 0 || !isAlive()) {
            this.moveTick = 0;
            serverLevel.players().forEach(p -> {
                p.connection.send(new ClientboundStopSoundPacket(ModSounds.SIEGE_ENGINE_MOVE.getLocation(), SoundSource.AMBIENT));
            });
        }

        this.setCooldown(this.getCooldown() - 1);

        BlockDamageTracker.clean(serverLevel);

        if (this.getCooldown() <= 6 && !this.getAttackHappened()) {
            // Destroy blocks in front
            destroyBlocksInFront();

            this.setAttackHappened(true);

            serverLevel.players().forEach(p -> {
                double distance = p.position().distanceTo(this.position());

                if (distance <= 40) {
                    float volume;

                    if (distance <= 5) {
                        volume = 1.0f;
                    } else {
                        float t = (float)((distance - 5) / 35.0); // normalize 6–40 -> 0–1
                        volume = 1.0f - t;                     // fades out
                    }

                    if (volume > 0f) {
                        p.playNotifySound(ModSounds.RAM_IMPACT, SoundSource.AMBIENT, volume, random.nextFloat(0.75f, 1.25f));
                    }
                }
            });
        }
    }

    private void destroyBlocksInFront() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        Vec3 lookVec = this.getViewVector(1.0F).normalize();
        Vec3 front = this.position().add(lookVec.scale(2.5));

        int baseX = (int) Math.floor(front.x);
        int baseY = (int) Math.floor(this.getY() + 1);
        int baseZ = (int) Math.floor(front.z);

        double radius = 1.5;
        Vec3 boxCenter = new Vec3(baseX + 0.5, baseY, baseZ + 0.5);
        Vec3 knockbackDir = this.getViewVector(1.0F).normalize().scale(2.5);

        float baseDamage = (float) getBaseDamage();

        serverLevel.getEntities(this,
                new AABB(
                        boxCenter.x - radius, boxCenter.y - 1, boxCenter.z - radius,
                        boxCenter.x + radius, boxCenter.y + 2, boxCenter.z + radius
                ),
                entity -> entity instanceof LivingEntity && !(entity instanceof Player playerEntity && playerEntity.isCreative())
        ).forEach(entity -> {
            entity.hurt(serverLevel.damageSources().mobAttack((LivingEntity) this.getOwner()), baseDamage);
            Vec3 knockback = new Vec3(knockbackDir.x, 0.25, knockbackDir.z);
            entity.addDeltaMovement(knockback);
            entity.hurtMarked = true;
        });

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                    BlockState state = serverLevel.getBlockState(pos);

                    if (state.isAir() || state.getDestroySpeed(serverLevel, pos) < 0) continue;

                    float hardness = state.getDestroySpeed(serverLevel, pos);
                    float damageFactor = 1.0f / (40 / baseDamage);

                    BlockDamageTracker.damageBlock(serverLevel, pos, damageFactor, hardness);
                }
            }
        }
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        if (entity instanceof Horse) {
            return new Vec3(0.0, 0.0, -1.25); // Left, Up, Back
        }
        return new Vec3(0.0, 0.0, 1.25);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, -0.7f, 0.0);
    }
}