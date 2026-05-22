package banduty.kingdomsieges;

import banduty.kingdomsieges.client.CannonBallRenderer;
import banduty.kingdomsieges.client.TrebuchetProjectileRenderer;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.entity.client.batteringram.BatteringRamRenderer;
import banduty.kingdomsieges.entity.client.cannon.CannonRenderer;
import banduty.kingdomsieges.entity.client.mangonel.MangonelRenderer;
import banduty.kingdomsieges.entity.client.mantlet.MantletRenderer;
import banduty.kingdomsieges.entity.client.ribauldequin.RibauldequinRenderer;
import banduty.kingdomsieges.entity.client.trebuchet.TrebuchetRenderer;
import banduty.kingdomsieges.event.HumanoidModelAnglesHandler;
import banduty.stoneycore.event.custom.HumanoidModelSetupAnimEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class KingdomSiegesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(KSEntities.CANNON_BALL.get(), CannonBallRenderer::new);
        EntityRendererRegistry.register(KSEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        EntityRendererRegistry.register(KSEntities.BATTERING_RAM_ENTITY.get(), BatteringRamRenderer::new);
        EntityRendererRegistry.register(KSEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinRenderer::new);
        EntityRendererRegistry.register(KSEntities.MANGONEL_ENTITY.get(), MangonelRenderer::new);
        EntityRendererRegistry.register(KSEntities.TREBUCHET_ENTITY.get(), TrebuchetRenderer::new);
        EntityRendererRegistry.register(KSEntities.TREBUCHET_PROJECTILE.get(), TrebuchetProjectileRenderer::new);
        EntityRendererRegistry.register(KSEntities.MANTLET_ENTITY.get(), MantletRenderer::new);

        HumanoidModelSetupAnimEvents.AFTER.register(new HumanoidModelAnglesHandler());
    }
}
