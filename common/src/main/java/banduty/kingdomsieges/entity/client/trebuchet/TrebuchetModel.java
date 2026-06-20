package banduty.kingdomsieges.entity.client.trebuchet;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.TrebuchetEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TrebuchetModel extends GeoModel<TrebuchetEntity> {
    @Override
    public ResourceLocation getModelResource(TrebuchetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "geo/trebuchet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TrebuchetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "textures/entity/trebuchet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TrebuchetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "animations/trebuchet.animation.json");
    }

    @Override
    public void setCustomAnimations(TrebuchetEntity animatable, long instanceId, AnimationState<TrebuchetEntity> animationState) {
        getBone("wheels_front").ifPresent(geoBone -> {
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

        Item loadedItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(animatable.getAmmoLoaded()));
        getBone("projectile").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || loadedItem != Items.STONE));
        getBone("projectile_magma").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || loadedItem != Items.MAGMA_BLOCK));
        getBone("projectile_rotten").ifPresent(geoBone -> geoBone.setHidden(animatable.getAmmoLoaded() == null || loadedItem != Items.ROTTEN_FLESH));
    }
}
