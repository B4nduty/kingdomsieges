package banduty.kingdomsieges.event;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.util.servertick.BellRinger;
import banduty.stoneycore.lands.util.LandState;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EndWorldTickHandler {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
            LandState landState = LandState.get(serverLevel);
            BellRinger.tick(serverLevel, landState);
        }
    }
}
