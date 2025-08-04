package banduty.kingdomsieges.entity.client.ribauldequin;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.RibauldequinEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RibauldequinModel extends GeoModel<RibauldequinEntity> {
    @Override
    public Identifier getModelResource(RibauldequinEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/ribauldequin.geo.json");
    }

    @Override
    public Identifier getTextureResource(RibauldequinEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/ribauldequin.png");
    }

    @Override
    public Identifier getAnimationResource(RibauldequinEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/ribauldequin.animation.json");
    }

    @Override
    public void setCustomAnimations(RibauldequinEntity animatable, long instanceId, AnimationState<RibauldequinEntity> animationState) {
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
