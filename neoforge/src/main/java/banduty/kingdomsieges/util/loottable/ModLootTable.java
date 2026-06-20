package banduty.kingdomsieges.util.loottable;

import banduty.kingdomsieges.Kingdomsieges;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public interface ModLootTable {
    DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Kingdomsieges.MOD_ID);

    DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<StructureLootModifier>> STRUCTURE_LOOT_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("add_structure_items", StructureLootModifier.CODEC);

    static void registerLootTables(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        Kingdomsieges.LOG.info("Registering Mod Loot Table for " + Kingdomsieges.MOD_ID);
    }
}
