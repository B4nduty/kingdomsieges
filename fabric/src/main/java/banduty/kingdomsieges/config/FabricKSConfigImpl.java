package banduty.kingdomsieges.config;

import banduty.kingdomsieges.KingdomSiegesFabric;

public class FabricKSConfigImpl extends KSConfigImpl {
    @Override
    public Choices getBellRingTime() {
        return KingdomSiegesFabric.getConfig().bellRingTime();
    }

    @Override
    public int getCannonRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.cannonRange();
    }

    @Override
    public int getRibauldequinRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.ribauldequinRange();
    }

    @Override
    public int getBatteringRamRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.batteringRamRange();
    }

    @Override
    public int getMangonelRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.mangonelRange();
    }

    @Override
    public int getTrebuchetRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.trebuchetRange();
    }

    @Override
    public int getMantletRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.mantletRange();
    }

    @Override
    public int getCannonBallRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.cannonBallRange();
    }

    @Override
    public int getTrebuchetProjectileRange() {
        return KingdomSiegesFabric.getConfig().clientOptions.trebuchetProjectileRange();
    }
}