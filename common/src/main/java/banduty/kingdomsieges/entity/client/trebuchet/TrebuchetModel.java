package banduty.kingdomsieges.entity.client.trebuchet;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.TrebuchetEntity;
import net.minecraft.resources.ResourceLocation;
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
        super.setCustomAnimations(animatable, instanceId, animationState);

        Item loaded = animatable.getLoadedAmmo();
        getBone("projectile").ifPresent(bone -> bone.setHidden(loaded != Items.STONE));
        getBone("projectile_magma").ifPresent(bone -> bone.setHidden(loaded != Items.MAGMA_BLOCK));
        getBone("projectile_rotten").ifPresent(bone -> bone.setHidden(loaded != Items.ROTTEN_FLESH));
    }
}
