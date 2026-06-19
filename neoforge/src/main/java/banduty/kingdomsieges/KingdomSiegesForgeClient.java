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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KingdomSiegesForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CANNON_BALL.get(), CannonBallRenderer::new);
        event.registerEntityRenderer(ModEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        event.registerEntityRenderer(ModEntities.BATTERING_RAM_ENTITY.get(), BatteringRamRenderer::new);
        event.registerEntityRenderer(ModEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinRenderer::new);
        event.registerEntityRenderer(ModEntities.MANGONEL_ENTITY.get(), MangonelRenderer::new);
        event.registerEntityRenderer(ModEntities.TREBUCHET_ENTITY.get(), TrebuchetRenderer::new);
        event.registerEntityRenderer(ModEntities.TREBUCHET_PROJECTILE.get(), TrebuchetProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.MANTLET_ENTITY.get(), MantletRenderer::new);
    }
}