package banduty.kingdomsieges.platform;

import banduty.kingdomsieges.config.FabricKSConfigImpl;
import banduty.kingdomsieges.config.KSConfigImpl;
import banduty.kingdomsieges.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public KSConfigImpl getConfig() {
        return new FabricKSConfigImpl();
    }
}
