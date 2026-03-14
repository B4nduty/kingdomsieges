package banduty.kingdomsieges.datagen;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.items.KSItems;
import banduty.stoneycore.datagen.ForgeModelProviderPlus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModModelProvider extends ForgeModelProviderPlus {

    public ModModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Kingdomsieges.MOD_ID, existingFileHelper);
    }

    @Override
    public void registerModels() {
        Item[] simpleItem = {
                KSItems.CANNON_SPAWNER.get(), KSItems.BATTERING_RAM_SPAWNER.get(), KSItems.RIBAULDEQUIN_SPAWNER.get(),
                KSItems.MANGONEL_SPAWNER.get(), KSItems.TREBUCHET_SPAWNER.get(), KSItems.MANTLET_SPAWNER.get(),
                KSItems.RAMROD.get(), KSItems.CANNON_MANUSCRIPT.get(), KSItems.BATTERING_RAM_MANUSCRIPT.get(),
                KSItems.RIBAULDEQUIN_MANUSCRIPT.get(), KSItems.MANGONEL_MANUSCRIPT.get(),
                KSItems.TREBUCHET_MANUSCRIPT.get(), KSItems.MANTLET_MANUSCRIPT.get()
        };

        for (Item item : simpleItem) simpleItem(item);
    }

    private void simpleItem(Item item) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        withExistingParent(path, "item/generated")
                .texture("layer0", modLoc("item/" + path));
    }
}
