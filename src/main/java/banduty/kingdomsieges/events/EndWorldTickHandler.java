package banduty.kingdomsieges.events;

import banduty.kingdomsieges.util.servertick.BellRinger;
import banduty.stoneycore.lands.util.LandState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

public class EndWorldTickHandler implements ServerTickEvents.EndWorldTick {
    @Override
    public void onEndTick(ServerWorld serverWorld) {
        LandState landState = LandState.get(serverWorld);
        BellRinger.tick(serverWorld, landState);
    }
}
