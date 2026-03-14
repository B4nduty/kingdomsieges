package banduty.kingdomsieges.config;

import banduty.kingdomsieges.KingdomSiegesForge;

public class ForgeKSConfigImpl extends KSConfigImpl {

    @Override
    public Choices getBellRingTime() {
        return KingdomSiegesForge.CONFIG.common.bellRingTime;
    }
}