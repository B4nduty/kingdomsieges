package banduty.kingdomsieges.entity.client.ribauldequin;

import banduty.kingdomsieges.entity.custom.sieges.RibauldequinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RibauldequinRenderer extends GeoEntityRenderer<RibauldequinEntity> {
    public RibauldequinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RibauldequinModel());
    }

    @Override
    public boolean shouldShowName(RibauldequinEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(RibauldequinEntity animatable,
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