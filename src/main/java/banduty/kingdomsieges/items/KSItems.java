package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.stoneycore.items.item.BlueprintItem;
import banduty.stoneycore.items.item.SiegeSpawnerItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;

public class KSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Kingdomsieges.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> CANNON_SPAWNER = ITEMS.register("cannon_spawner", () ->
            new SiegeSpawnerItem(ModEntities.CANNON_ENTITY, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> BATTERING_RAM_SPAWNER = ITEMS.register("battering_ram_spawner", () ->
            new SiegeSpawnerItem(ModEntities.BATTERING_RAM_ENTITY, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> RIBAULDEQUIN_SPAWNER = ITEMS.register("ribauldequin_spawner", () ->
            new SiegeSpawnerItem(ModEntities.RIBAULDEQUIN_ENTITY, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> MANGONEL_SPAWNER = ITEMS.register("mangonel_spawner", () ->
            new SiegeSpawnerItem(ModEntities.MANGONEL_ENTITY, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> TREBUCHET_SPAWNER = ITEMS.register("trebuchet_spawner", () ->
            new SiegeSpawnerItem(ModEntities.TREBUCHET_ENTITY, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> MANTLET_SPAWNER = ITEMS.register("mantlet_spawner", () ->
            new SiegeSpawnerItem(ModEntities.MANTLET_ENTITY, new Item.Settings().maxCount(1)));

    public static final RegistrySupplier<Item> RAMROD = ITEMS.register("ramrod", () ->
            new Item(new Item.Settings().maxCount(1).maxDamage(256)));

    public static final RegistrySupplier<Item> CANNON_MANUSCRIPT = ITEMS.register("cannon_manuscript", () ->
            new BlueprintItem(ModStructures.CANNON_STRUCTURE, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> BATTERING_RAM_MANUSCRIPT = ITEMS.register("battering_ram_manuscript", () ->
            new BlueprintItem(ModStructures.BATTERING_RAM_STRUCTURE, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> RIBAULDEQUIN_MANUSCRIPT = ITEMS.register("ribauldequin_manuscript", () ->
            new BlueprintItem(ModStructures.RIBAULDEQUIN_STRUCTURE, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> MANGONEL_MANUSCRIPT = ITEMS.register("mangonel_manuscript", () ->
            new BlueprintItem(ModStructures.MANGONEL_STRUCTURE, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> TREBUCHET_MANUSCRIPT = ITEMS.register("trebuchet_manuscript", () ->
            new BlueprintItem(ModStructures.TREBUCHET_STRUCTURE, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> MANTLET_MANUSCRIPT = ITEMS.register("mantlet_manuscript", () ->
            new BlueprintItem(ModStructures.MANTLET_STRUCTURE, new Item.Settings().maxCount(1)));

    public static void registerItems() {
        ITEMS.register();
        Kingdomsieges.LOGGER.info("Registering Mod Items for " + Kingdomsieges.MOD_ID);
    }
}
