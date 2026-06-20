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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Kingdomsieges.MOD_ID, value = Dist.CLIENT)
public class KingdomSiegesNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(KSEntities.CANNON_BALL.get(), CannonBallRenderer::new);
        event.registerEntityRenderer(KSEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        event.registerEntityRenderer(KSEntities.BATTERING_RAM_ENTITY.get(), BatteringRamRenderer::new);
        event.registerEntityRenderer(KSEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinRenderer::new);
        event.registerEntityRenderer(KSEntities.MANGONEL_ENTITY.get(), MangonelRenderer::new);
        event.registerEntityRenderer(KSEntities.TREBUCHET_ENTITY.get(), TrebuchetRenderer::new);
        event.registerEntityRenderer(KSEntities.TREBUCHET_PROJECTILE.get(), TrebuchetProjectileRenderer::new);
        event.registerEntityRenderer(KSEntities.MANTLET_ENTITY.get(), MantletRenderer::new);
    }
}