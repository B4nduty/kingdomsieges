package banduty.kingdomsieges.event;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.util.servertick.BellRinger;
import banduty.stoneycore.lands.util.LandState;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EndWorldTickHandler {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            LandState landState = LandState.get(serverLevel);
            BellRinger.tick(serverLevel, landState);
        }
    }
}