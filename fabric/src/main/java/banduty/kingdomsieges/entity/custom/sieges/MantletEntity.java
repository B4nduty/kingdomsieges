package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MantletEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("mantlet")
            .health(300.0)
            .speed(0.01)
            .knockbackResist(265.0)
            .moveSound(ModSounds.SIEGE_ENGINE_MOVE)
            .moveSoundDelay(150)
            .moveSoundRange(30.0)
            .build();

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation setupAnim = RawAnimation.begin().then("set_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation pickupAnim = RawAnimation.begin().then("pick_up", Animation.LoopType.HOLD_ON_LAST_FRAME);

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
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public InteractionResult handleSiegeInteraction(Player player, InteractionHand hand, ServerLevel serverLevel) {
        if (getFirstPassenger() != null) return InteractionResult.FAIL;

        if (canAddPassenger(player) && !player.isShiftKeyDown()) {
            player.startRiding(this);
            setOwner(player);
            stopAnimation("set_up");
            triggerAnimation("pick_up");
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        if (!isPicked()) {
            stopAnimation("pick_up");
            triggerAnimation("set_up");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", state -> PlayState.STOP)
                .triggerableAnim("set_up", setupAnim)
                .triggerableAnim("pick_up", pickupAnim));
    }

    @Override
    public void triggerAnimation(String name) {
        if ("set_up".equals(name)) triggerAnim("anim_controller", "set_up");
        if ("pick_up".equals(name)) triggerAnim("anim_controller", "pick_up");
    }

    @Override
    public void stopAnimation(String name) {
        if ("set_up".equals(name)) stopTriggeredAnimation("anim_controller", "set_up");
        if ("pick_up".equals(name)) stopTriggeredAnimation("anim_controller", "pick_up");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -2.0) : new Vec3(0.0, 0.0, 2.5);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, -0.7f, 0.0);
    }
}