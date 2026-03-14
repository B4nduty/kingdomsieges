package banduty.kingdomsieges.client;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.model.CannonBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CannonBallRenderer extends EntityRenderer<CannonProjectile> {
    public CannonBallRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(CannonProjectile entity, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        poseStack.pushPose();
        EntityModel<Entity> model = new CannonBall(CannonBall.getTexturedModelData().bakeRoot());

        poseStack.translate(0.0, -1.375, 0.0);

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(this.getTextureLocation(entity)));
        model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation(CannonProjectile entity) {
        return new ResourceLocation(Kingdomsieges.MOD_ID, "textures/entity/cannon_ball.png");
    }
}
