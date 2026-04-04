package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.ModLands;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.kingdomsieges.util.loottable.ModLootTable;
import banduty.stoneycore.items.SiegeSpawnerItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Kingdomsieges.MOD_ID)
public class KingdomSiegesForge {

    public KingdomSiegesForge() {
        Kingdomsieges.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        KSConfigs.loadConfig(KSConfigs.SPEC, FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get()).resolve(Kingdomsieges.MOD_ID + "-common.toml"));


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
            ModLands.registerLands();
            SiegeSpawnerItem.register(ModEntities.CANNON_ENTITY.get(), KSItems.CANNON_SPAWNER.get());
            SiegeSpawnerItem.register(ModEntities.BATTERING_RAM_ENTITY.get(), KSItems.BATTERING_RAM_SPAWNER.get());
            SiegeSpawnerItem.register(ModEntities.RIBAULDEQUIN_ENTITY.get(), KSItems.RIBAULDEQUIN_SPAWNER.get());
            SiegeSpawnerItem.register(ModEntities.MANGONEL_ENTITY.get(), KSItems.MANGONEL_SPAWNER.get());
            SiegeSpawnerItem.register(ModEntities.TREBUCHET_ENTITY.get(), KSItems.TREBUCHET_SPAWNER.get());
            SiegeSpawnerItem.register(ModEntities.MANTLET_ENTITY.get(), KSItems.MANTLET_SPAWNER.get());
        });
    }
}