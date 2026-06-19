package banduty.kingdomsieges;

import banduty.kingdomsieges.config.IKSConfig;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.KSLands;
import banduty.kingdomsieges.platform.Services;
import banduty.kingdomsieges.sounds.KSSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kingdomsieges {

    public static final String MOD_ID = "kingdomsieges";
    public static final String MOD_NAME = "Kingdoms & Sieges";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        KSEntities.init();
        KSItems.init();
        KSItemGroups.registerItemGroups();
        KSSounds.init();
        KSLands.registerLands();

        if (Services.PLATFORM.isModLoaded(Kingdomsieges.MOD_ID)) {
            LOG.info("Hello to Kingdoms & Sieges");
        }
    }

    public static IKSConfig getConfig() {
        return Services.PLATFORM.getConfig();
    }
}