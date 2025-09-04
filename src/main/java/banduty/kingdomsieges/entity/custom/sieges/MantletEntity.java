package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import java.util.UUID;

public class MantletEntity extends AbstractSiegeEntity implements GeoEntity {
    private int moveTick;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation setup = RawAnimation.begin().then("set_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation pickup = RawAnimation.begin().then("pick_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    protected static final TrackedData<Boolean> PICKED;

    static {
        PICKED = DataTracker.registerData(MantletEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public MantletEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.01)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PICKED, false);
    }

    public void setPicked(boolean picked) {
        this.dataTracker.set(PICKED, picked);
    }

    public boolean getPicked() {
        return this.dataTracker.get(PICKED);
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

        ItemStack heldItem = player.getStackInHand(hand);

        if (this.getFirstPassenger() instanceof MobEntity mobEntity) {
            if (heldItem.isOf(Items.SHEARS)) {
                mobEntity.stopRiding();

                double[] offset = calculateHorseDismountOffset(mobEntity);
                double dX = this.getX() + offset[0];
                double dY = this.getY() + 0.5;
                double dZ = this.getZ() + offset[1];
                mobEntity.setPos(dX, dY, dZ);

                ItemEntity leadEntity = new ItemEntity(this.getWorld(), dX, dY, dZ, new ItemStack(Items.LEAD));
                this.getWorld().spawnEntity(leadEntity);

                player.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            player.startRiding(mobEntity);
            return ActionResult.SUCCESS;
        }

        if (this.getPassengerList().isEmpty()) {
            List<Entity> nearby = this.getWorld().getOtherEntities(player, this.getBoundingBox().expand(4.0),
                    entity -> entity instanceof MobEntity);

            for (Entity entity : nearby) {
                if (canAddPassenger(entity) && entity instanceof MobEntity mobEntity && mobEntity.getHoldingEntity() == player &&
                        !(mobEntity instanceof TameableEntity tameableEntity && !tameableEntity.isTamed()) &&
                        (!(entity instanceof Saddleable saddleable) || saddleable.isSaddled())) {
                    mobEntity.detachLeash(true, false);
                    mobEntity.stopRiding();
                    mobEntity.startRiding(this);
                    this.stopTriggeredAnimation("anim_controller", "set_up");
                    this.triggerAnim("anim_controller", "pick_up");
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (this.getFirstPassenger() != null) return ActionResult.FAIL;

        if (this.canAddPassenger(player) && !player.isSneaking()) {
            player.startRiding(this);
            this.setOwner(player);
            this.stopTriggeredAnimation("anim_controller", "set_up");
            this.triggerAnim("anim_controller", "pick_up");
            return ActionResult.SUCCESS;
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();

        this.setPicked(this.getFirstPassenger() != null);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (!this.getPicked()) {
                this.stopTriggeredAnimation("anim_controller", "pick_up");
                this.triggerAnim("anim_controller", "set_up");
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
    public Vec3d getPassengerOffset(Entity entity) {
        if (entity instanceof HorseEntity) {
            return new Vec3d(0.0, 0.0, -2.0); // Left, Up, Back
        }
        return new Vec3d(0.0, 0.0, 2.5);
    }

    @Override
    public double getVelocity(Entity entity) {
        if (entity instanceof HorseEntity) {
            return 0.02d;
        }
        return 0.01d;
    }

    @Override
    public Vec3d getPlayerPOV() {
        return new Vec3d(0.0, -0.7f, 0.0);
    }
}