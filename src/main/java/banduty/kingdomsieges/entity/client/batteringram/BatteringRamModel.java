package banduty.kingdomsieges.entity.client.batteringram;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.BatteringRamEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BatteringRamModel extends GeoModel<BatteringRamEntity> {
    @Override
    public Identifier getModelResource(BatteringRamEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "geo/battering_ram.geo.json");
    }

    @Override
    public Identifier getTextureResource(BatteringRamEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/battering_ram.png");
    }

    @Override
    public Identifier getAnimationResource(BatteringRamEntity animatable) {
        return new Identifier(Kingdomsieges.MOD_ID, "animations/battering_ram.animation.json");
    }

    @Override
    public void setCustomAnimations(BatteringRamEntity animatable, long instanceId, AnimationState<BatteringRamEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof HorseEntity) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheels_middle").ifPresent(geoBone -> {
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
    }
}
