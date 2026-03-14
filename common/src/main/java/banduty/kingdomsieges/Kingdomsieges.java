package banduty.kingdomsieges;

import banduty.kingdomsieges.config.IKSConfig;
import banduty.kingdomsieges.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kingdomsieges {

    public static final String MOD_ID = "kingdomsieges";
    public static final String MOD_NAME = "Kingdoms & Sieges";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        if (Services.PLATFORM.isModLoaded(Kingdomsieges.MOD_ID)) {
            LOG.info("Hello to Kingdoms & Sieges");
        }
    }

    public static IKSConfig getConfig() {
        return Services.PLATFORM.getConfig();
    }
}