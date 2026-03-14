package banduty.kingdomsieges.util.loottable;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class StructureLootModifier {
    public static void registerLootTableModifications() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

            // Add to Village Blacksmith Chest
            if (id.equals(new ResourceLocation("minecraft", "chests/village/village_weaponsmith"))) {
                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.9f))
                        .add(LootItem.lootTableItem(KSItems.MANTLET_MANUSCRIPT))
                        .add(LootItem.lootTableItem(KSItems.MANGONEL_MANUSCRIPT))
                        .build();
                tableBuilder.pool(pool);
            }

            // Add to Pillager Outpost Chest
            if (id.equals(new ResourceLocation("minecraft", "chests/pillager_outpost"))) {
                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.75f))
                        .add(LootItem.lootTableItem(KSItems.TREBUCHET_MANUSCRIPT))
                        .add(LootItem.lootTableItem(KSItems.BATTERING_RAM_MANUSCRIPT))
                        .build();
                tableBuilder.pool(pool);
            }

            // Add to Woodland Mansion Chest
            if (id.equals(new ResourceLocation("minecraft", "chests/woodland_mansion"))) {
                LootPool pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                        .add(LootItem.lootTableItem(KSItems.RIBAULDEQUIN_MANUSCRIPT))
                        .add(LootItem.lootTableItem(KSItems.CANNON_MANUSCRIPT))
                        .build();
                tableBuilder.pool(pool);
            }
        });
    }
}