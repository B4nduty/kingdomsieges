package banduty.kingdomsieges.util.loottable;

import banduty.kingdomsieges.Kingdomsieges;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ModLootTable {
    DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Kingdomsieges.MOD_ID);

    RegistryObject<Codec<StructureLootModifier>> STRUCTURE_LOOT_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("add_structure_items", StructureLootModifier.CODEC);

    static void registerLootTables(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        Kingdomsieges.LOG.info("Registering Mod Loot Table for " + Kingdomsieges.MOD_ID);
    }
}
