package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MantletEntity extends AbstractSiegeEntity implements GeoEntity {
    private int moveTick;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation setup = RawAnimation.begin().then("set_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation pickup = RawAnimation.begin().then("pick_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    protected static final EntityDataAccessor<Boolean> PICKED;

    static {
        PICKED = SynchedEntityData.defineId(MantletEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public MantletEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.01)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PICKED, false);
    }

    public void setPicked(boolean picked) {
        this.entityData.set(PICKED, picked);
    }

    public boolean getPicked() {
            return this.entityData.get(PICKED);
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

        ItemStack heldItem = player.getItemInHand(hand);

        if (this.getFirstPassenger() instanceof Mob mob) {
            if (heldItem.is(Items.SHEARS)) {
                mob.stopRiding();

                double[] offset = calculateHorseDismountOffset(mob);
                double dX = this.getX() + offset[0];
                double dY = this.getY() + 0.5;
                double dZ = this.getZ() + offset[1];
                mob.setPos(dX, dY, dZ);

                ItemEntity leadEntity = new ItemEntity(this.level(), dX, dY, dZ, new ItemStack(Items.LEAD));
                this.level().addFreshEntity(leadEntity);

                player.playNotifySound(SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            player.startRiding(mob);
            return InteractionResult.SUCCESS;
        }

        if (this.getPassengers().isEmpty()) {
            List<Entity> nearby = this.level().getEntities(player, this.getBoundingBox().inflate(4.0),
                    entity -> entity instanceof Mob);

            for (Entity entity : nearby) {
                if (canAddPassenger(entity) && entity instanceof Mob mob && mob.getLeashHolder() == player &&
                        !(mob instanceof TamableAnimal tameableEntity && !tameableEntity.isTame()) &&
                        (!(entity instanceof Saddleable saddleable) || saddleable.isSaddled())) {
                    mob.dropLeash(true, false);
                    mob.stopRiding();
                    mob.startRiding(this);
                    this.stopTriggeredAnimation("anim_controller", "set_up");
                    this.triggerAnim("anim_controller", "pick_up");
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (this.getFirstPassenger() != null) return InteractionResult.FAIL;

        if (this.canAddPassenger(player) && !player.isShiftKeyDown()) {
            player.startRiding(this);
            this.setOwner(player);
            this.stopTriggeredAnimation("anim_controller", "set_up");
            this.triggerAnim("anim_controller", "pick_up");
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();

        this.setPicked(this.getFirstPassenger() != null);

        if (this.level() instanceof ServerLevel serverLevel) {
            if (!this.getPicked()) {
                this.stopTriggeredAnimation("anim_controller", "pick_up");
                this.triggerAnim("anim_controller", "set_up");
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
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"anim_controller", state -> PlayState.STOP)
                .triggerableAnim("set_up", setup)
                .triggerableAnim("pick_up", pickup));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        if (entity instanceof Horse) {
            return new Vec3(0.0, 0.0, -2.0); // Left, Up, Back
        }
        return new Vec3(0.0, 0.0, 2.5);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, -0.7f, 0.0);
    }
}