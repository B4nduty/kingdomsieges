package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.stoneycore.items.SiegeSpawnerItem;
import banduty.stoneycore.items.blueprint.BlueprintItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface KSItems {
    DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Kingdomsieges.MOD_ID);

    // Required for your own mod entities
    RegistryObject<Item> CANNON_SPAWNER = ITEMS.register("cannon_spawner", () ->
            new SiegeSpawnerItem(ModEntities.CANNON_ENTITY, new Item.Properties()));
    RegistryObject<Item> BATTERING_RAM_SPAWNER = ITEMS.register("battering_ram_spawner", () ->
            new SiegeSpawnerItem(ModEntities.BATTERING_RAM_ENTITY, new Item.Properties()));
    RegistryObject<Item> RIBAULDEQUIN_SPAWNER = ITEMS.register("ribauldequin_spawner", () ->
            new SiegeSpawnerItem(ModEntities.RIBAULDEQUIN_ENTITY, new Item.Properties()));
    RegistryObject<Item> MANGONEL_SPAWNER = ITEMS.register("mangonel_spawner", () ->
            new SiegeSpawnerItem(ModEntities.MANGONEL_ENTITY, new Item.Properties()));
    RegistryObject<Item> TREBUCHET_SPAWNER = ITEMS.register("trebuchet_spawner", () ->
            new SiegeSpawnerItem(ModEntities.TREBUCHET_ENTITY, new Item.Properties()));
    RegistryObject<Item> MANTLET_SPAWNER = ITEMS.register("mantlet_spawner", () ->
            new SiegeSpawnerItem(ModEntities.MANTLET_ENTITY, new Item.Properties()));

    RegistryObject<Item> RAMROD = ITEMS.register("ramrod", () ->
            new Item(new Item.Properties().stacksTo(1).defaultDurability(256)));

    RegistryObject<Item> CANNON_MANUSCRIPT = ITEMS.register("cannon_manuscript", () ->
            new BlueprintItem(ModStructures.CANNON_STRUCTURE, new Item.Properties().stacksTo(1)));
    RegistryObject<Item> BATTERING_RAM_MANUSCRIPT = ITEMS.register("battering_ram_manuscript", () ->
            new BlueprintItem(ModStructures.BATTERING_RAM_STRUCTURE, new Item.Properties().stacksTo(1)));
    RegistryObject<Item> RIBAULDEQUIN_MANUSCRIPT = ITEMS.register("ribauldequin_manuscript", () ->
            new BlueprintItem(ModStructures.RIBAULDEQUIN_STRUCTURE, new Item.Properties().stacksTo(1)));
    RegistryObject<Item> MANGONEL_MANUSCRIPT = ITEMS.register("mangonel_manuscript", () ->
            new BlueprintItem(ModStructures.MANGONEL_STRUCTURE, new Item.Properties().stacksTo(1)));
    RegistryObject<Item> TREBUCHET_MANUSCRIPT = ITEMS.register("trebuchet_manuscript", () ->
            new BlueprintItem(ModStructures.TREBUCHET_STRUCTURE, new Item.Properties().stacksTo(1)));
    RegistryObject<Item> MANTLET_MANUSCRIPT = ITEMS.register("mantlet_manuscript", () ->
            new BlueprintItem(ModStructures.MANTLET_STRUCTURE, new Item.Properties().stacksTo(1)));

    static void registerItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
        Kingdomsieges.LOG.info("Registering Mod Items for " + Kingdomsieges.MOD_ID);
    }
}
