package banduty.kingdomsieges.entity.client.cannon;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.CannonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CannonModel extends GeoModel<CannonEntity> {
    @Override
    public ResourceLocation getModelResource(CannonEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "geo/cannon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CannonEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "textures/entity/cannon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CannonEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "animations/cannon.animation.json");
    }

    @Override
    public void setCustomAnimations(CannonEntity animatable, long instanceId, AnimationState<CannonEntity> animationState) {
        getBone("cannon").ifPresent(geoBone -> geoBone.setRotX((float) Math.toRadians(-animatable.getTrackedPitch() - 20)));

        getBone("wheel_left").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheel_right").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof Horse)));

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
