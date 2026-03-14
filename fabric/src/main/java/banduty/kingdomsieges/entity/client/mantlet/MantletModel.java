package banduty.kingdomsieges.entity.client.mantlet;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.MantletEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MantletModel extends GeoModel<MantletEntity> {
    @Override
    public ResourceLocation getModelResource(MantletEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "geo/mantlet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MantletEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "textures/entity/mantlet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MantletEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "animations/mantlet.animation.json");
    }

    @Override
    public void setCustomAnimations(MantletEntity animatable, long instanceId, AnimationState<MantletEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof Horse)));

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
