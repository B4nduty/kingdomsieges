package banduty.kingdomsieges.util.loottable;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents; // Updated to v3 if using latest Fabric API, or keep v2 depending on your build gradle. v3 is standard for 1.21+
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class StructureLootModifier {
    public static void registerLootTableModifications() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Add to Village Blacksmith Chest
            if (key.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith"))) {
                LootPool.Builder pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.9f))
                        .add(LootItem.lootTableItem(KSItems.MANTLET_MANUSCRIPT.get()))
                        .add(LootItem.lootTableItem(KSItems.MANGONEL_MANUSCRIPT.get()));
                tableBuilder.pool(pool.build());
            }

            // Add to Pillager Outpost Chest
            if (key.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"))) {
                LootPool.Builder pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.75f))
                        .add(LootItem.lootTableItem(KSItems.TREBUCHET_MANUSCRIPT.get()))
                        .add(LootItem.lootTableItem(KSItems.BATTERING_RAM_MANUSCRIPT.get()));
                tableBuilder.pool(pool.build());
            }

            // Add to Woodland Mansion Chest
            if (key.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"))) {
                LootPool.Builder pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                        .add(LootItem.lootTableItem(KSItems.RIBAULDEQUIN_MANUSCRIPT.get()))
                        .add(LootItem.lootTableItem(KSItems.CANNON_MANUSCRIPT.get()));
                tableBuilder.pool(pool.build());
            }
        });
    }
}