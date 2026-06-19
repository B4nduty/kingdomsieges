package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.stoneycore.items.itemgroup.SCItemGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import banduty.kingdomsieges.platform.Services;

import java.util.List;
import java.util.function.Supplier;

public interface KSItemGroups {

    ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "textures/gui/container/creative_inventory/tab_items.png");

    ResourceLocation SCROLLER_SPRITE = ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/scroller");
    ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/scroller_disabled");

    ResourceLocation[] UNSELECTED_TOP_TABS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_1"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_2"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_3"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_4"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_5"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_6"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_unselected_7")
    };
    ResourceLocation[] SELECTED_TOP_TABS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_1"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_2"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_3"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_4"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_5"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_6"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_top_selected_7")
    };
    ResourceLocation[] UNSELECTED_BOTTOM_TABS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_1"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_2"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_3"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_4"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_5"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_6"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_unselected_7")
    };
    ResourceLocation[] SELECTED_BOTTOM_TABS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_1"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_2"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_3"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_4"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_5"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_6"),
            ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "creative_inventory/tab_bottom_selected_7")
    };

    private static ItemStack itemStack(ItemLike item) {
        return new ItemStack(item);
    }

    Supplier<CreativeModeTab> KS_SIEGE_ENGINES_TAB = register("ks_siege_engines", () -> SCItemGroup.create(ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "ks_siege_engines"))
            .icon(() -> new ItemStack(KSItems.CANNON_SPAWNER.get()))
            .backgroundTexture(BACKGROUND)
            .scrollerSprites(SCROLLER_SPRITE, SCROLLER_DISABLED_SPRITE)
            .topTabSprites(UNSELECTED_TOP_TABS, SELECTED_TOP_TABS)
            .bottomTabSprites(UNSELECTED_BOTTOM_TABS, SELECTED_BOTTOM_TABS)
            .title(Component.translatable("component.itemgroup.kingdomsieges.tab.ks_siege_engines"))
            .appendItems((output) -> output.acceptAll(List.of(
                    itemStack(KSItems.CANNON_SPAWNER.get()),
                    itemStack(KSItems.BATTERING_RAM_SPAWNER.get()),
                    itemStack(KSItems.RIBAULDEQUIN_SPAWNER.get()),
                    itemStack(KSItems.MANGONEL_SPAWNER.get()),
                    itemStack(KSItems.TREBUCHET_SPAWNER.get()),
                    itemStack(KSItems.MANTLET_SPAWNER.get())
            )))
            .build());

    Supplier<CreativeModeTab> KS_ITEMS_TAB = register("ks_items", () -> SCItemGroup.create(ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, "ks_items"))
            .icon(() -> new ItemStack(KSItems.RAMROD.get()))
            .backgroundTexture(BACKGROUND)
            .scrollerSprites(SCROLLER_SPRITE, SCROLLER_DISABLED_SPRITE)
            .topTabSprites(UNSELECTED_TOP_TABS, SELECTED_TOP_TABS)
            .bottomTabSprites(UNSELECTED_BOTTOM_TABS, SELECTED_BOTTOM_TABS)
            .title(Component.translatable("component.itemgroup.kingdomsieges.tab.ks_items"))
            .appendItems((output) -> output.acceptAll(List.of(
                    itemStack(KSItems.RAMROD.get()),
                    itemStack(KSItems.CANNON_MANUSCRIPT.get()),
                    itemStack(KSItems.BATTERING_RAM_MANUSCRIPT.get()),
                    itemStack(KSItems.RIBAULDEQUIN_MANUSCRIPT.get()),
                    itemStack(KSItems.MANGONEL_MANUSCRIPT.get()),
                    itemStack(KSItems.TREBUCHET_MANUSCRIPT.get()),
                    itemStack(KSItems.MANTLET_MANUSCRIPT.get())
            )))
            .build());

    private static Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> itemSupplier) {
        return Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB, name, itemSupplier);
    }

    static void registerItemGroups() {
        Kingdomsieges.LOG.info("Registering ItemGroups for " + Kingdomsieges.MOD_ID);
    }
}