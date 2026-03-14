package banduty.kingdomsieges.event;

import banduty.kingdomsieges.util.servertick.BellRinger;
import banduty.stoneycore.lands.util.LandState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;

public class EndWorldTickHandler implements ServerTickEvents.EndWorldTick {
    @Override
    public void onEndTick(ServerLevel serverLevel) {
        LandState landState = LandState.get(serverLevel);
        BellRinger.tick(serverLevel, landState);
    }
}
