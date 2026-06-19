package banduty.kingdomsieges.entity.client.mantlet;

import banduty.kingdomsieges.entity.custom.sieges.MantletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MantletRenderer extends GeoEntityRenderer<MantletEntity> {
    public MantletRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MantletModel());
    }

    @Override
    public boolean shouldShowName(MantletEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(MantletEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable.getFirstPassenger() instanceof Horse) {
            poseStack.translate(0, 0, 3f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }
    }
}