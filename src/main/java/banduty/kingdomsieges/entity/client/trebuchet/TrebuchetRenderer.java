package banduty.kingdomsieges.entity.client.trebuchet;

import banduty.kingdomsieges.entity.custom.sieges.TrebuchetEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TrebuchetRenderer extends GeoEntityRenderer<TrebuchetEntity> {
    public TrebuchetRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TrebuchetModel());
    }

    @Override
    public boolean hasLabel(TrebuchetEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(TrebuchetEntity animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-animatable.getYaw() - 180.0F));
    }
}