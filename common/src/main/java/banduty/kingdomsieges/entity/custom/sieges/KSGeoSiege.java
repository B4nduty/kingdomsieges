package banduty.kingdomsieges.entity.custom.sieges;

import banduty.stoneycore.entity.siege.SiegeAnimatable;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.Map;

public interface KSGeoSiege extends GeoEntity, SiegeAnimatable {

    String CONTROLLER = "anim_controller";

    default Map<SiegeAnimation, RawAnimation> triggeredAnimations() {
        return Map.of();
    }

    @Nullable
    default RawAnimation stateAnimation() {
        return null;
    }

    default double animationSpeed() {
        return 1.0;
    }

    @Override
    default void triggerSiegeAnimation(SiegeAnimation animation) {
        if (triggeredAnimations().containsKey(animation)) triggerAnim(CONTROLLER, animation.id());
    }

    @Override
    default void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        AnimationController<KSGeoSiege> controller = new AnimationController<>(this, CONTROLLER, 0, state -> {
            if (state.getController().isPlayingTriggeredAnimation()) return PlayState.CONTINUE;
            RawAnimation animation = stateAnimation();
            return animation == null ? PlayState.STOP : state.setAndContinue(animation);
        });
        controller.setAnimationSpeed(animationSpeed());
        triggeredAnimations().forEach((key, animation) -> controller.triggerableAnim(key.id(), animation));
        registrar.add(controller);
    }
}
