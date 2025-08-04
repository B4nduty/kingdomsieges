package banduty.kingdomsieges.util.loottable;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class StructureLootModifier {
    public static void registerLootTableModifications() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

            // Add to Village Blacksmith Chest
            if (id.equals(new Identifier("minecraft", "chests/village/village_weaponsmith"))) {
                LootPool pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1, 1))
                        .conditionally(RandomChanceLootCondition.builder(0.9f))
                        .with(ItemEntry.builder(KSItems.MANTLET_MANUSCRIPT.get()))
                        .with(ItemEntry.builder(KSItems.MANGONEL_MANUSCRIPT.get()))
                        .build();
                tableBuilder.pool(pool);
            }

            // Add to Pillager Outpost Chest
            if (id.equals(new Identifier("minecraft", "chests/pillager_outpost"))) {
                LootPool pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1, 1))
                        .conditionally(RandomChanceLootCondition.builder(0.75f))
                        .with(ItemEntry.builder(KSItems.TREBUCHET_MANUSCRIPT.get()))
                        .with(ItemEntry.builder(KSItems.BATTERING_RAM_MANUSCRIPT.get()))
                        .build();
                tableBuilder.pool(pool);
            }

            // Add to Woodland Mansion Chest
            if (id.equals(new Identifier("minecraft", "chests/woodland_mansion"))) {
                LootPool pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1, 1))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .with(ItemEntry.builder(KSItems.RIBAULDEQUIN_MANUSCRIPT.get()))
                        .with(ItemEntry.builder(KSItems.CANNON_MANUSCRIPT.get()))
                        .build();
                tableBuilder.pool(pool);
            }
        });
    }
}