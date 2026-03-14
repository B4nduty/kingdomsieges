package banduty.kingdomsieges;

import banduty.kingdomsieges.client.CannonBallRenderer;
import banduty.kingdomsieges.client.TrebuchetProjectileRenderer;
import banduty.kingdomsieges.entity.ModEntities;
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
        EntityRendererRegistry.register(ModEntities.CANNON_BALL, CannonBallRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANNON_ENTITY, CannonRenderer::new);
        EntityRendererRegistry.register(ModEntities.BATTERING_RAM_ENTITY, BatteringRamRenderer::new);
        EntityRendererRegistry.register(ModEntities.RIBAULDEQUIN_ENTITY, RibauldequinRenderer::new);
        EntityRendererRegistry.register(ModEntities.MANGONEL_ENTITY, MangonelRenderer::new);
        EntityRendererRegistry.register(ModEntities.TREBUCHET_ENTITY, TrebuchetRenderer::new);
        EntityRendererRegistry.register(ModEntities.TREBUCHET_PROJECTILE, TrebuchetProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.MANTLET_ENTITY, MantletRenderer::new);

        HumanoidModelSetupAnimEvents.AFTER.register(new HumanoidModelAnglesHandler());
    }
}
