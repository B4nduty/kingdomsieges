package banduty.kingdomsieges.datagen;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(KSItems.CANNON_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.BATTERING_RAM_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.RIBAULDEQUIN_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.MANGONEL_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.TREBUCHET_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.MANTLET_SPAWNER.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.RAMROD.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.CANNON_MANUSCRIPT.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.BATTERING_RAM_MANUSCRIPT.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.RIBAULDEQUIN_MANUSCRIPT.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.MANGONEL_MANUSCRIPT.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.TREBUCHET_MANUSCRIPT.get(), Models.HANDHELD);
        itemModelGenerator.register(KSItems.MANTLET_MANUSCRIPT.get(), Models.HANDHELD);
    }
}
