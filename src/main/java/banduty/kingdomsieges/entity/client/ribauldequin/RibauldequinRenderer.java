package banduty.kingdomsieges.entity.client.ribauldequin;

import banduty.kingdomsieges.entity.custom.sieges.RibauldequinEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RibauldequinRenderer extends GeoEntityRenderer<RibauldequinEntity> {
    public RibauldequinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RibauldequinModel());
    }

    @Override
    public boolean hasLabel(RibauldequinEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(RibauldequinEntity animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable.getFirstPassenger() instanceof HorseEntity) {
            poseStack.translate(0, 0, 3f);
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
    }
}