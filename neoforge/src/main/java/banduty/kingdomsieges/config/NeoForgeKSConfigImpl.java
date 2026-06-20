package banduty.kingdomsieges.config;

public class NeoForgeKSConfigImpl extends KSConfigImpl {

    @Override
    public Choices getBellRingTime() {
        return KSConfigs.bellRingTime.get();
    }

    @Override
    public int getCannonRange() {
        return 100;
    }

    @Override
    public int getRibauldequinRange() {
        return 100;
    }

    @Override
    public int getBatteringRamRange() {
        return 100;
    }

    @Override
    public int getMangonelRange() {
        return 100;
    }

    @Override
    public int getTrebuchetRange() {
        return 100;
    }

    @Override
    public int getMantletRange() {
        return 100;
    }

    @Override
    public int getCannonBallRange() {
        return 100;
    }

    @Override
    public int getTrebuchetProjectileRange() {
        return 100;
    }
}