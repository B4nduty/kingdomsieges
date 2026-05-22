package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.platform.Services;
import banduty.kingdomsieges.structure.KSStructures;
import banduty.stoneycore.items.custom.SiegeSpawnerItem;
import banduty.stoneycore.items.custom.blueprint.BlueprintItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface KSItems {
    Supplier<Item> CANNON_SPAWNER = registerItem("cannon_spawner",
            () -> new SiegeSpawnerItem(KSEntities.CANNON_ENTITY.get(), new Item.Properties()));
    Supplier<Item> BATTERING_RAM_SPAWNER = registerItem("battering_ram_spawner",
            () -> new SiegeSpawnerItem(KSEntities.BATTERING_RAM_ENTITY.get(), new Item.Properties()));
    Supplier<Item> RIBAULDEQUIN_SPAWNER = registerItem("ribauldequin_spawner",
            () -> new SiegeSpawnerItem(KSEntities.RIBAULDEQUIN_ENTITY.get(), new Item.Properties()));
    Supplier<Item> MANGONEL_SPAWNER = registerItem("mangonel_spawner",
            () -> new SiegeSpawnerItem(KSEntities.MANGONEL_ENTITY.get(), new Item.Properties()));
    Supplier<Item> TREBUCHET_SPAWNER = registerItem("trebuchet_spawner",
            () -> new SiegeSpawnerItem(KSEntities.TREBUCHET_ENTITY.get(), new Item.Properties()));
    Supplier<Item> MANTLET_SPAWNER = registerItem("mantlet_spawner",
            () -> new SiegeSpawnerItem(KSEntities.MANTLET_ENTITY.get(), new Item.Properties()));

    Supplier<Item> RAMROD = registerItem("ramrod",
            () -> new Item(new Item.Properties().stacksTo(1).durability(256)));

    Supplier<Item> CANNON_MANUSCRIPT = registerItem("cannon_manuscript",
            () -> new BlueprintItem(KSStructures.CANNON_STRUCTURE, new Item.Properties().stacksTo(1)));
    Supplier<Item> BATTERING_RAM_MANUSCRIPT = registerItem("battering_ram_manuscript",
            () -> new BlueprintItem(KSStructures.BATTERING_RAM_STRUCTURE, new Item.Properties().stacksTo(1)));
    Supplier<Item> RIBAULDEQUIN_MANUSCRIPT = registerItem("ribauldequin_manuscript",
            () -> new BlueprintItem(KSStructures.RIBAULDEQUIN_STRUCTURE, new Item.Properties().stacksTo(1)));
    Supplier<Item> MANGONEL_MANUSCRIPT = registerItem("mangonel_manuscript",
            () -> new BlueprintItem(KSStructures.MANGONEL_STRUCTURE, new Item.Properties().stacksTo(1)));
    Supplier<Item> TREBUCHET_MANUSCRIPT = registerItem("trebuchet_manuscript",
            () -> new BlueprintItem(KSStructures.TREBUCHET_STRUCTURE, new Item.Properties().stacksTo(1)));
    Supplier<Item> MANTLET_MANUSCRIPT = registerItem("mantlet_manuscript",
            () -> new BlueprintItem(KSStructures.MANTLET_STRUCTURE, new Item.Properties().stacksTo(1)));

    private static Supplier<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return Services.PLATFORM.register(BuiltInRegistries.ITEM, name, itemSupplier);
    }

    static void init() {
        Kingdomsieges.LOG.info("Registering Mod Items for " + Kingdomsieges.MOD_ID);
    }
}
