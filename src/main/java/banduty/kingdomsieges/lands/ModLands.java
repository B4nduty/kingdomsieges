package banduty.kingdomsieges.lands;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.lands.LandType;
import banduty.stoneycore.lands.LandTypeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModLands {
    public static LandType KINGDOM;

    public static void registerLands() {
        KINGDOM = LandTypeRegistry.register(
                new Identifier(Kingdomsieges.MOD_ID, "kingdom"),
                Blocks.BELL,
                SCItems.CROWN.get(),
                25,
                Map.of(
                        Items.EMERALD, 1,
                        Items.EMERALD_BLOCK, 9
                ),
                "radius - 24",
                LandType.TerrainType.GROUND
        );

        Kingdomsieges.LOGGER.info("Registering Lands for " + Kingdomsieges.MOD_ID);
    }
}