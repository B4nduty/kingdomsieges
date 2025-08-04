package banduty.kingdomsieges.entity.client.mangonel;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.MangonelEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MangonelModel extends GeoModel<MangonelEntity> {
    @Override
    public Identifier getModelResource(MangonelEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/mangonel.geo.json");
    }

    @Override
    public Identifier getTextureResource(MangonelEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/mangonel.png");
    }

    @Override
    public Identifier getAnimationResource(MangonelEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/mangonel.animation.json");
    }

    @Override
    public void setCustomAnimations(MangonelEntity animatable, long instanceId, AnimationState<MangonelEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheels_back").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof HorseEntity)));

        super.setCustomAnimations(animatable, instanceId, animationState);

        getBone("load").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || !(animatable.getAmmoLoaded().equals("stone"))));
        getBone("load_magma").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || !(animatable.getAmmoLoaded().equals("magma"))));
    }
}
