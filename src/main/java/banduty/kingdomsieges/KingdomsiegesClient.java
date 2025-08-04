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
import banduty.kingdomsieges.events.BipedEntityModelAnglesHandler;
import banduty.stoneycore.event.custom.BipedEntityModelAnglesEvents;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class KingdomsiegesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (Platform.isForge()) {
            ClientLifecycleEvent.CLIENT_SETUP.register(minecraftClient -> {
                registerClient();
            });
        } else {
            registerClient();
        }
    }

    private void registerClient() {
        EntityRendererRegistry.register(ModEntities.CANNON_BALL.get(), CannonBallRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        EntityRendererRegistry.register(ModEntities.BATTERING_RAM_ENTITY.get(), BatteringRamRenderer::new);
        EntityRendererRegistry.register(ModEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinRenderer::new);
        EntityRendererRegistry.register(ModEntities.MANGONEL_ENTITY.get(), MangonelRenderer::new);
        EntityRendererRegistry.register(ModEntities.TREBUCHET_ENTITY.get(), TrebuchetRenderer::new);
        EntityRendererRegistry.register(ModEntities.TREBUCHET_PROJECTILE.get(), TrebuchetProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.MANTLET_ENTITY.get(), MantletRenderer::new);

        BipedEntityModelAnglesEvents.BEFORE.register(new BipedEntityModelAnglesHandler());
    }
}
