package banduty.kingdomsieges.entity.client.mangonel;

import banduty.kingdomsieges.entity.custom.sieges.MangonelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MangonelRenderer extends GeoEntityRenderer<MangonelEntity> {
    public MangonelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new banduty.kingdomsieges.entity.client.mangonel.MangonelModel());
    }

    @Override
    public boolean shouldShowName(MangonelEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(MangonelEntity animatable,
                                  PoseStack poseStack,
                                  float ageInTicks,
                                  float rotationYaw,
                                  float partialTick,
                                  float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);

        if (animatable.getFirstPassenger() instanceof Horse) {
            poseStack.translate(0.0F, 0.0F, 3.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
    }
}