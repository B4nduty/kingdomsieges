package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.lands.KSLands;
import banduty.kingdomsieges.platform.NeoForgePlatformHelper;
import banduty.kingdomsieges.util.loottable.ModLootTable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Kingdomsieges.MOD_ID)
public class KingdomSiegesNeoForge {

    public KingdomSiegesNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Kingdomsieges.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, KSConfigs.SPEC);

        ModLootTable.registerLootTables(modEventBus);

        NeoForgePlatformHelper.registerRegistries(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(KSLands::registerLands);
    }
}