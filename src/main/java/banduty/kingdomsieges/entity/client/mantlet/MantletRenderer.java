package banduty.kingdomsieges.entity.client.mantlet;

import banduty.kingdomsieges.entity.custom.sieges.MantletEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MantletRenderer extends GeoEntityRenderer<MantletEntity> {
    public MantletRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MantletModel());
    }

    @Override
    public boolean hasLabel(MantletEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(MantletEntity animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable.getFirstPassenger() instanceof HorseEntity) {
            poseStack.translate(0, 0, 3f);
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
    }
}