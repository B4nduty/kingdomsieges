package banduty.kingdomsieges.client;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.model.CannonBall;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class CannonBallRenderer extends EntityRenderer<CannonProjectile> {
    public CannonBallRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void render(CannonProjectile entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        EntityModel<Entity> model = new CannonBall(CannonBall.getTexturedModelData().createModel());

        matrices.translate(0.0, -1.375, 0.0);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getTexture(entity)));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();
    }

    public Identifier getTexture(CannonProjectile entity) {
        return new Identifier(Kingdomsieges.MOD_ID, "textures/entity/cannon_ball.png");
    }
}
