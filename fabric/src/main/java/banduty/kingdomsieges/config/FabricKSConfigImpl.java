package banduty.kingdomsieges.config;

import banduty.kingdomsieges.KingdomSiegesFabric;

public class FabricKSConfigImpl extends KSConfigImpl {
    @Override
    public Choices getBellRingTime() {
        return KingdomSiegesFabric.getConfig().bellRingTime();
    }
}