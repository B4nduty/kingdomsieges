package banduty.kingdomsieges.config;

public class ForgeKSConfigImpl extends KSConfigImpl {

    @Override
    public Choices getBellRingTime() {
        return KSConfigs.bellRingTime.get();
    }
}