package banduty.kingdomsieges.entity.client.batteringram;

import banduty.kingdomsieges.entity.custom.sieges.BatteringRamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.horse.Horse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BatteringRamRenderer extends GeoEntityRenderer<BatteringRamEntity> {
    public BatteringRamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BatteringRamModel());
    }

    @Override
    public boolean shouldShowName(BatteringRamEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(BatteringRamEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable.getFirstPassenger() instanceof Horse) {
            poseStack.translate(0, 0, 3f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }
    }
}