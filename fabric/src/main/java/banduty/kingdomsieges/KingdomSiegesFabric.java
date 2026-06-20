package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.event.EndWorldTickHandler;
import banduty.kingdomsieges.lands.KSLands;
import banduty.kingdomsieges.util.loottable.StructureLootModifier;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class KingdomSiegesFabric implements ModInitializer {
    public static KSConfigs CONFIG;

    @Override
    public void onInitialize() {
        AutoConfig.register(KSConfigs.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KSConfigs.class).getConfig();

        StructureLootModifier.registerLootTableModifications();
        
        Kingdomsieges.LOG.info("Registering Entities Attributes for " + Kingdomsieges.MOD_ID);
        FabricDefaultAttributeRegistry.register(KSEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KSEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes());

        ServerTickEvents.END_WORLD_TICK.register(new EndWorldTickHandler());

        Kingdomsieges.init();

        KSLands.registerLands();
    }
}
