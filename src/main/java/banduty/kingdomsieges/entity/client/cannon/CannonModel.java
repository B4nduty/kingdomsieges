package banduty.kingdomsieges.entity.client.cannon;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.CannonEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CannonModel extends GeoModel<CannonEntity> {
    @Override
    public Identifier getModelResource(CannonEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/cannon.geo.json");
    }

    @Override
    public Identifier getTextureResource(CannonEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/cannon.png");
    }

    @Override
    public Identifier getAnimationResource(CannonEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/cannon.animation.json");
    }

    @Override
    public void setCustomAnimations(CannonEntity animatable, long instanceId, AnimationState<CannonEntity> animationState) {
        getBone("cannon").ifPresent(geoBone -> geoBone.setRotX((float) Math.toRadians(-animatable.getTrackedPitch() - 20)));

        getBone("wheel_left").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheel_right").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof HorseEntity)));

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
