package banduty.kingdomsieges.platform;

import banduty.kingdomsieges.config.ForgeKSConfigImpl;
import banduty.kingdomsieges.config.KSConfigImpl;
import banduty.kingdomsieges.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public KSConfigImpl getConfig() {
        return new ForgeKSConfigImpl();
    }
}