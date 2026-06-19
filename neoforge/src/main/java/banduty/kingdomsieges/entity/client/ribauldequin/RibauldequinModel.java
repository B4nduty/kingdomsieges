package banduty.kingdomsieges.entity.client.ribauldequin;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.RibauldequinEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RibauldequinModel extends GeoModel<RibauldequinEntity> {
    @Override
    public ResourceLocation getModelResource(RibauldequinEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "geo/ribauldequin.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RibauldequinEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "textures/entity/ribauldequin.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RibauldequinEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "animations/ribauldequin.animation.json");
    }

    @Override
    public void setCustomAnimations(RibauldequinEntity animatable, long instanceId, AnimationState<RibauldequinEntity> animationState) {
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
