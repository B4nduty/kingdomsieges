package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.ModLands;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.kingdomsieges.util.loottable.ModLootTable;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Kingdomsieges.MOD_ID)
public class KingdomSiegesForge {
    public static KSConfigs CONFIG;

    public KingdomSiegesForge() {
        Kingdomsieges.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AutoConfig.register(KSConfigs.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        CONFIG = AutoConfig.getConfigHolder(KSConfigs.class).getConfig();

        ModEntities.register(modEventBus);
        KSItems.registerItems(modEventBus);
        KSItemGroups.register(modEventBus);
        ModLootTable.registerLootTables(modEventBus);

        ModSounds.register(modEventBus);
        ModStructures.registerStructures();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModLands.init();
        });
    }
}