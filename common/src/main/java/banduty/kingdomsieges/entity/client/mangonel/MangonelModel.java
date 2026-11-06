package banduty.kingdomsieges.entity.client.mangonel;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.MangonelEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MangonelModel extends GeoModel<MangonelEntity> {
    @Override
    public ResourceLocation getModelResource(MangonelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "geo/mangonel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MangonelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "textures/entity/mangonel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MangonelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "animations/mangonel.animation.json");
    }

    @Override
    public void setCustomAnimations(MangonelEntity animatable, long instanceId, AnimationState<MangonelEntity> animationState) {
        boolean horseDrawn = animatable.getFirstPassenger() instanceof Horse;

        if (animatable.getCooldown() == 0) {
            float wheelRotation = (float) Math.toRadians(horseDrawn ? -animatable.getWheelRotation() : animatable.getWheelRotation());
            getBone("wheels_front").ifPresent(bone -> bone.setRotX(wheelRotation));
            getBone("wheels_back").ifPresent(bone -> bone.setRotX(wheelRotation));
        }

        getBone("lead").ifPresent(bone -> bone.setHidden(!horseDrawn));

        super.setCustomAnimations(animatable, instanceId, animationState);

        Item loaded = animatable.getLoadedAmmo();
        getBone("load").ifPresent(bone -> bone.setHidden(loaded != Items.STONE));
        getBone("load_magma").ifPresent(bone -> bone.setHidden(loaded != Items.MAGMA_BLOCK));
    }
}
