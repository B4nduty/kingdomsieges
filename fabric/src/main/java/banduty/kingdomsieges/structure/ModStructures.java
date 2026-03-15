package banduty.kingdomsieges.structure;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.structure.custom.*;
import banduty.stoneycore.structure.StructureSpawnRegistry;
import banduty.stoneycore.structure.StructureSpawner;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface ModStructures {
    StructureSpawner CANNON_STRUCTURE = register("cannon", CannonStructure::new);
    StructureSpawner BATTERING_RAM_STRUCTURE = register("battering_ram", BatteringRamStructure::new);
    StructureSpawner MANGONEL_STRUCTURE = register("mangonel", MangonelStructure::new);
    StructureSpawner RIBAULDEQUIN_STRUCTURE = register("ribauldequin", RibauldequinStructure::new);
    StructureSpawner TREBUCHET_STRUCTURE = register("trebuchet", TrebuchetStructure::new);
    StructureSpawner MANTLET_STRUCTURE = register("mantlet", MantletStructure::new);

    private static StructureSpawner register(String id, Supplier<StructureSpawner> spawner) {
        ResourceLocation resourceLocation = new ResourceLocation(Kingdomsieges.MOD_ID, id);
        StructureSpawner structure = spawner.get();
        StructureSpawnRegistry.register(resourceLocation, structure);
        return structure;
    }

    static void registerStructures() {
        Kingdomsieges.LOG.info("Registering Structures for " + Kingdomsieges.MOD_ID);
    }
}