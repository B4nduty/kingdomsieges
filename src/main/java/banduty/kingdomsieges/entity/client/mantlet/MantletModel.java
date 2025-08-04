package banduty.kingdomsieges.entity.client.mantlet;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.MantletEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MantletModel extends GeoModel<MantletEntity> {
    @Override
    public Identifier getModelResource(MantletEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/mantlet.geo.json");
    }

    @Override
    public Identifier getTextureResource(MantletEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/mantlet.png");
    }

    @Override
    public Identifier getAnimationResource(MantletEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/mantlet.animation.json");
    }

    @Override
    public void setCustomAnimations(MantletEntity animatable, long instanceId, AnimationState<MantletEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof HorseEntity)));

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
