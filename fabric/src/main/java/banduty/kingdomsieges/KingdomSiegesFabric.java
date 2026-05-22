package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KingdomSiegesConfig;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.event.EndWorldTickHandler;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.util.loottable.StructureLootModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class KingdomSiegesFabric implements ModInitializer {
    private static final KingdomSiegesConfig CONFIG = KingdomSiegesConfig.createAndLoad();

    @Override
    public void onInitialize() {
        Kingdomsieges.init();

        KSItemGroups.registerItemGroups();
        StructureLootModifier.registerLootTableModifications();
        
        Kingdomsieges.LOG.info("Registering Entities Attributes for " + Kingdomsieges.MOD_ID);
        FabricDefaultAttributeRegistry.register(KSEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes());

        banduty.kingdomsieges.structure.KSStructures.registerStructures();

        ServerTickEvents.END_WORLD_TICK.register(new EndWorldTickHandler());
    }

    public static KingdomSiegesConfig getConfig() {
        return CONFIG;
    }
}
