package banduty.kingdomsieges.entity.client.trebuchet;

import banduty.kingdomsieges.entity.custom.sieges.TrebuchetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TrebuchetRenderer extends GeoEntityRenderer<TrebuchetEntity> {
    public TrebuchetRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TrebuchetModel());
    }

    @Override
    public boolean shouldShowName(TrebuchetEntity animatable) {
        return false;
    }

    @Override
    protected void applyRotations(TrebuchetEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot() - 180.0F));
    }
}