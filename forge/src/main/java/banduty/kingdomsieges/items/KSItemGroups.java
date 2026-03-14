package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public interface KSItemGroups {
    DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kingdomsieges.MOD_ID);

    private static ItemStack itemStack(ItemLike item) {
        return new ItemStack(item);
    }

    RegistryObject<CreativeModeTab> KS_ITEMS_TAB = CREATIVE_MODE_TABS.register("ks_items",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(KSItems.RAMROD.get()))
                    .title(Component.translatable("component.itemgroup.kingdomsieges.tab.ks_items"))
                    .displayItems((parameters, output) -> {
                        output.acceptAll(List.of(
                                itemStack(KSItems.RAMROD.get()),
                                itemStack(KSItems.CANNON_MANUSCRIPT.get()),
                                itemStack(KSItems.BATTERING_RAM_MANUSCRIPT.get()),
                                itemStack(KSItems.RIBAULDEQUIN_MANUSCRIPT.get()),
                                itemStack(KSItems.MANGONEL_MANUSCRIPT.get()),
                                itemStack(KSItems.TREBUCHET_MANUSCRIPT.get()),
                                itemStack(KSItems.MANTLET_MANUSCRIPT.get())
                        ));
                    }).build());

    RegistryObject<CreativeModeTab> KS_SIEGE_ENGINES_TAB = CREATIVE_MODE_TABS.register("ks_siege_engines",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(KSItems.CANNON_SPAWNER.get()))
                    .title(Component.translatable("component.itemgroup.kingdomsieges.tab.ks_siege_engines"))
                    .displayItems((parameters, output) -> {
                        output.acceptAll(List.of(
                                itemStack(KSItems.CANNON_SPAWNER.get()),
                                itemStack(KSItems.BATTERING_RAM_SPAWNER.get()),
                                itemStack(KSItems.RIBAULDEQUIN_SPAWNER.get()),
                                itemStack(KSItems.MANGONEL_SPAWNER.get()),
                                itemStack(KSItems.TREBUCHET_SPAWNER.get()),
                                itemStack(KSItems.MANTLET_SPAWNER.get())
                        ));
                    }).build());

    static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}