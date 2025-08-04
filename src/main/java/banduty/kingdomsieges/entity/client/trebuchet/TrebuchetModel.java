package banduty.kingdomsieges.entity.client.trebuchet;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.TrebuchetEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TrebuchetModel extends GeoModel<TrebuchetEntity> {
    @Override
    public Identifier getModelResource(TrebuchetEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/trebuchet.geo.json");
    }

    @Override
    public Identifier getTextureResource(TrebuchetEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/trebuchet.png");
    }

    @Override
    public Identifier getAnimationResource(TrebuchetEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/trebuchet.animation.json");
    }

    @Override
    public void setCustomAnimations(TrebuchetEntity animatable, long instanceId, AnimationState<TrebuchetEntity> animationState) {
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

        getBone("projectile").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || !(animatable.getAmmoLoaded().equals("stone"))));
        getBone("projectile_magma").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || !(animatable.getAmmoLoaded().equals("magma"))));
        getBone("projectile_rotten").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || !(animatable.getAmmoLoaded().equals("rotten"))));
    }
}
