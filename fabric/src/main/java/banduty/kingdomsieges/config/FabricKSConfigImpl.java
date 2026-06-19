package banduty.kingdomsieges.config;

import banduty.kingdomsieges.KingdomSiegesFabric;

public class FabricKSConfigImpl extends KSConfigImpl {
    @Override
    public Choices getBellRingTime() {
        return KingdomSiegesFabric.CONFIG.bellRingTime;
    }

    @Override
    public int getCannonRange() {
        return KingdomSiegesFabric.CONFIG.cannonRange;
    }

    @Override
    public int getRibauldequinRange() {
        return KingdomSiegesFabric.CONFIG.ribauldequinRange;
    }

    @Override
    public int getBatteringRamRange() {
        return KingdomSiegesFabric.CONFIG.batteringRamRange;
    }

    @Override
    public int getMangonelRange() {
        return KingdomSiegesFabric.CONFIG.mangonelRange;
    }

    @Override
    public int getTrebuchetRange() {
        return KingdomSiegesFabric.CONFIG.trebuchetRange;
    }

    @Override
    public int getMantletRange() {
        return KingdomSiegesFabric.CONFIG.mantletRange;
    }

    @Override
    public int getCannonBallRange() {
        return KingdomSiegesFabric.CONFIG.cannonBallRange;
    }

    @Override
    public int getTrebuchetProjectileRange() {
        return KingdomSiegesFabric.CONFIG.trebuchetProjectileRange;
    }
}