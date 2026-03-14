package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.ModLands;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.kingdomsieges.util.loottable.ModLootTable;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Kingdomsieges.MOD_ID)
public class KingdomSiegesForge {
    public static KSConfigs CONFIG;
    
    public KingdomSiegesForge() {
        Kingdomsieges.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.registerEntities();
        KSItems.registerItems(modEventBus);
        KSItemGroups.register(modEventBus);
        ModLands.registerLands();
        ModLootTable.registerLootTables(modEventBus);

        ModSounds.registerSounds();
        ModStructures.registerStructures();
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        Kingdomsieges.LOG.info("Registering Entities Attributes for " + Kingdomsieges.MOD_ID);
        event.put(ModEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes().build());
        event.put(ModEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes().build());
        event.put(ModEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes().build());
        event.put(ModEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes().build());
        event.put(ModEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes().build());
        event.put(ModEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes().build());
    }
}