package banduty.kingdomsieges.entity.client.batteringram;

import banduty.kingdomsieges.entity.custom.sieges.BatteringRamEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BatteringRamRenderer extends GeoEntityRenderer<BatteringRamEntity> {
    public BatteringRamRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BatteringRamModel());
    }

    @Override
    public boolean hasLabel(BatteringRamEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(BatteringRamEntity animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable.getFirstPassenger() instanceof HorseEntity) {
            poseStack.translate(0, 0, 3f);
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
    }
}