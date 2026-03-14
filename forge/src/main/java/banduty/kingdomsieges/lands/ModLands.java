package banduty.kingdomsieges.lands;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.stoneycore.items.SCItems;
import banduty.stoneycore.lands.LandType;
import banduty.stoneycore.lands.LandTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public interface ModLands {
    LandType KINGDOM = LandTypeRegistry.register(
            new ResourceLocation(Kingdomsieges.MOD_ID, "kingdom"),
            Blocks.BELL,
            SCItems.CROWN.get(),
            25,
            Map.of(
                    Items.EMERALD, 1,
                    Items.EMERALD_BLOCK, 9
            ),
            "radius - 24",
            LandType.TerrainType.GROUND,
            -1
    );

    static void registerLands() {
        Kingdomsieges.LOG.info("Registering Lands for " + Kingdomsieges.MOD_ID);
    }
}