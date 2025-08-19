package banduty.kingdomsieges;

import banduty.kingdomsieges.config.KingdomSiegesConfig;
import banduty.kingdomsieges.datagen.ModModelProvider;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.events.EndWorldTickHandler;
import banduty.kingdomsieges.items.KSItemGroups;
import banduty.kingdomsieges.items.KSItems;
import banduty.kingdomsieges.lands.ModLands;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.kingdomsieges.util.loottable.StructureLootModifier;
import dev.architectury.event.events.common.LifecycleEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kingdomsieges implements ModInitializer, DataGeneratorEntrypoint {
	public static final String MOD_ID = "kingdomsieges";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final KingdomSiegesConfig CONFIG = KingdomSiegesConfig.createAndLoad();

	@Override
	public void onInitialize() {
		ModEntities.registerEntities();
		KSItems.registerItems();

		LifecycleEvent.SETUP.register(() -> {
			KSItemGroups.registerItemGroups();
			ModLands.registerLands();
			StructureLootModifier.registerLootTableModifications();

			Kingdomsieges.LOGGER.info("Registering Entities Attributes for " + Kingdomsieges.MOD_ID);
			FabricDefaultAttributeRegistry.register(ModEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(ModEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(ModEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(ModEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(ModEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(ModEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes());
		});

		ModSounds.registerSounds();
		ModStructures.registerStructures();
		ServerTickEvents.END_WORLD_TICK.register(new EndWorldTickHandler());
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
	}

	public static KingdomSiegesConfig getConfig() {
		return CONFIG;
	}
}