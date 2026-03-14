package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KingdomSiegesConfig;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.event.EndWorldTickHandler;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.ModLands;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.kingdomsieges.util.loottable.StructureLootModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class KingdomSiegesFabric implements ModInitializer {
    private static final KingdomSiegesConfig CONFIG = KingdomSiegesConfig.createAndLoad();

    @Override
    public void onInitialize() {
        Kingdomsieges.init();

        ModEntities.registerEntities();
        KSItems.registerItems();
        KSItemGroups.registerItemGroups();
        ModLands.registerLands();
        StructureLootModifier.registerLootTableModifications();
        
        Kingdomsieges.LOG.info("Registering Entities Attributes for " + Kingdomsieges.MOD_ID);
        FabricDefaultAttributeRegistry.register(ModEntities.CANNON_ENTITY, CannonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.BATTERING_RAM_ENTITY, BatteringRamEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.RIBAULDEQUIN_ENTITY, RibauldequinEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MANGONEL_ENTITY, MangonelEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TREBUCHET_ENTITY, TrebuchetEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MANTLET_ENTITY, MantletEntity.createAttributes());
        
        ModSounds.registerSounds();
        ModStructures.registerStructures();

        ServerTickEvents.END_WORLD_TICK.register(new EndWorldTickHandler());
    }

    public static KingdomSiegesConfig getConfig() {
        return CONFIG;
    }
}
