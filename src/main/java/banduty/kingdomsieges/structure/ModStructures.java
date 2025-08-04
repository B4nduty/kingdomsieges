package banduty.kingdomsieges.structure;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.structure.custom.*;
import banduty.stoneycore.structure.StructureSpawnRegistry;
import banduty.stoneycore.structure.StructureSpawner;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModStructures {
    public static final StructureSpawner CANNON_STRUCTURE = register("cannon_structure", CannonStructure::new);
    public static final StructureSpawner BATTERING_RAM_STRUCTURE = register("battering_ram_structure", BatteringRamStructure::new);
    public static final StructureSpawner MANGONEL_STRUCTURE = register("mangonel_structure", MangonelStructure::new);
    public static final StructureSpawner RIBAULDEQUIN_STRUCTURE = register("ribauldequin_structure", RibauldequinStructure::new);
    public static final StructureSpawner TREBUCHET_STRUCTURE = register("trebuchet_structure", TrebuchetStructure::new);
    public static final StructureSpawner MANTLET_STRUCTURE = register("mantlet_structure", MantletStructure::new);

    private static StructureSpawner register(String id, Supplier<StructureSpawner> spawner) {
        Identifier identifier = new Identifier(Kingdomsieges.MOD_ID, id);
        StructureSpawner structure = spawner.get();
        StructureSpawnRegistry.register(identifier, structure);
        return structure;
    }

    public static void registerStructures() {
        Kingdomsieges.LOGGER.info("Registering Structures for " + Kingdomsieges.MOD_ID);
    }
}