package banduty.kingdomsieges.entity.client.batteringram;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.BatteringRamEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BatteringRamModel extends GeoModel<BatteringRamEntity> {
    @Override
    public ResourceLocation getModelResource(BatteringRamEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "geo/battering_ram.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BatteringRamEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "textures/entity/battering_ram.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BatteringRamEntity animatable) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "animations/battering_ram.animation.json");
    }

    @Override
    public void setCustomAnimations(BatteringRamEntity animatable, long instanceId, AnimationState<BatteringRamEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheels_middle").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("wheels_back").ifPresent(geoBone -> {
            if (animatable.getCooldown() != 0) return;
            float wheelRotation = animatable.getWheelRotation();
            if (animatable.getFirstPassenger() instanceof Horse) wheelRotation = -wheelRotation;
            geoBone.setRotX((float) Math.toRadians(wheelRotation));
        });

        getBone("lead").ifPresent(geoBone -> geoBone.setHidden(!(animatable.getFirstPassenger() instanceof Horse)));

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
