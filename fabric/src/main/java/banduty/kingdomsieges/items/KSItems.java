package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.structure.ModStructures;
import banduty.stoneycore.items.SiegeSpawnerItem;
import banduty.stoneycore.items.blueprint.BlueprintItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface KSItems {
    Item CANNON_SPAWNER =  registerItem("cannon_spawner", 
            new SiegeSpawnerItem(ModEntities.CANNON_ENTITY, new Item.Properties().stacksTo(1)));
    Item BATTERING_RAM_SPAWNER =  registerItem("battering_ram_spawner", 
            new SiegeSpawnerItem(ModEntities.BATTERING_RAM_ENTITY, new Item.Properties().stacksTo(1)));
    Item RIBAULDEQUIN_SPAWNER =  registerItem("ribauldequin_spawner", 
            new SiegeSpawnerItem(ModEntities.RIBAULDEQUIN_ENTITY, new Item.Properties().stacksTo(1)));
    Item MANGONEL_SPAWNER =  registerItem("mangonel_spawner", 
            new SiegeSpawnerItem(ModEntities.MANGONEL_ENTITY, new Item.Properties().stacksTo(1)));
    Item TREBUCHET_SPAWNER =  registerItem("trebuchet_spawner", 
            new SiegeSpawnerItem(ModEntities.TREBUCHET_ENTITY, new Item.Properties().stacksTo(1)));
    Item MANTLET_SPAWNER =  registerItem("mantlet_spawner", 
            new SiegeSpawnerItem(ModEntities.MANTLET_ENTITY, new Item.Properties().stacksTo(1)));

    Item RAMROD =  registerItem("ramrod", 
            new Item(new Item.Properties().stacksTo(1).defaultDurability(256)));

    Item CANNON_MANUSCRIPT =  registerItem("cannon_manuscript", 
            new BlueprintItem(ModStructures.CANNON_STRUCTURE, new Item.Properties().stacksTo(1)));
    Item BATTERING_RAM_MANUSCRIPT =  registerItem("battering_ram_manuscript", 
            new BlueprintItem(ModStructures.BATTERING_RAM_STRUCTURE, new Item.Properties().stacksTo(1)));
    Item RIBAULDEQUIN_MANUSCRIPT =  registerItem("ribauldequin_manuscript", 
            new BlueprintItem(ModStructures.RIBAULDEQUIN_STRUCTURE, new Item.Properties().stacksTo(1)));
    Item MANGONEL_MANUSCRIPT =  registerItem("mangonel_manuscript", 
            new BlueprintItem(ModStructures.MANGONEL_STRUCTURE, new Item.Properties().stacksTo(1)));
    Item TREBUCHET_MANUSCRIPT =  registerItem("trebuchet_manuscript", 
            new BlueprintItem(ModStructures.TREBUCHET_STRUCTURE, new Item.Properties().stacksTo(1)));
    Item MANTLET_MANUSCRIPT =  registerItem("mantlet_manuscript", 
            new BlueprintItem(ModStructures.MANTLET_STRUCTURE, new Item.Properties().stacksTo(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Kingdomsieges.MOD_ID, name), item);
    }

    static void registerItems() {
        Kingdomsieges.LOG.info("Registering Mod Items for " + Kingdomsieges.MOD_ID);
    }
}
