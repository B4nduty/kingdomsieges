package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.stoneycore.StoneyCore;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class KSItemGroups {
    private static final Identifier DISCORD_ICON = new Identifier(StoneyCore.MOD_ID, "textures/gui/button/discord.png");
    private static final Identifier BACKGROUND = new Identifier(StoneyCore.MOD_ID, "textures/gui/group_black.png");
    private static final Identifier TABS = new Identifier(StoneyCore.MOD_ID, "textures/gui/tabs_black.png");

    private static ItemStack itemStack(ItemConvertible item) {
        return new ItemStack(item);
    }

    private static void items(ItemGroup.DisplayContext ctx, ItemGroup.Entries stacks) {
        stacks.addAll(List.of(
                itemStack(KSItems.RAMROD.get()),
                itemStack(KSItems.CANNON_MANUSCRIPT.get()),
                itemStack(KSItems.BATTERING_RAM_MANUSCRIPT.get()),
                itemStack(KSItems.RIBAULDEQUIN_MANUSCRIPT.get()),
                itemStack(KSItems.MANGONEL_MANUSCRIPT.get()),
                itemStack(KSItems.TREBUCHET_MANUSCRIPT.get()),
                itemStack(KSItems.MANTLET_MANUSCRIPT.get())
        ));
    }

    private static void siegeMachines(ItemGroup.DisplayContext ctx, ItemGroup.Entries stacks) {
        stacks.addAll(List.of(
                itemStack(KSItems.CANNON_SPAWNER.get()),
                itemStack(KSItems.BATTERING_RAM_SPAWNER.get()),
                itemStack(KSItems.RIBAULDEQUIN_SPAWNER.get()),
                itemStack(KSItems.MANGONEL_SPAWNER.get()),
                itemStack(KSItems.TREBUCHET_SPAWNER.get()),
                itemStack(KSItems.MANTLET_SPAWNER.get())
        ));
    }

    public static final ItemGroupTab KS_SIEGE_MACHINES_TAB = new ItemGroupTab(
            Icon.of(KSItems.CANNON_SPAWNER.get()),
            Text.translatable("text.itemgroup.kingdomsieges.tab.ks_siege_machines").formatted(Formatting.WHITE),
            KSItemGroups::siegeMachines,
            TABS,
            true
    );

    public static final ItemGroupTab KS_ITEMS_TAB = new ItemGroupTab(
            Icon.of(KSItems.RAMROD.get()),
            Text.translatable("text.itemgroup.kingdomsieges.tab.ks_items").formatted(Formatting.WHITE),
            KSItemGroups::items,
            TABS,
            true
    );

    public static final OwoItemGroup KS_TAB = OwoItemGroup
        .builder(new Identifier(Kingdomsieges.MOD_ID, "ks_tab"), () -> Icon.of(new Identifier(Kingdomsieges.MOD_ID,
                "icon.png"), 0, 0, 16, 16))
            .customTexture(BACKGROUND)
            .initializer(KSItemGroups::initializeGroup)
            .displaySingleTab()
            .build();

    private static void initializeGroup(OwoItemGroup group) {
        group.tabs.add(KS_SIEGE_MACHINES_TAB);
        group.tabs.add(KS_ITEMS_TAB);
        group.addButton(LinkButton.discord("https://discord.gg/AbtCqntN9S"));
    }
    
    public static void registerItemGroups() {
        KS_TAB.initialize();
    }

    @Environment(EnvType.CLIENT)
    public static final class LinkButton implements OwoItemGroup.ButtonDefinition {
        private final Icon icon;
        private final Text tooltip;
        private final Identifier texture;

        public LinkButton(ItemGroup group, Icon icon, String name, Identifier texture) {
            this.icon = icon;
            this.tooltip = OwoItemGroup.ButtonDefinition.tooltipFor(group, "button", name);
            this.texture = texture;
        }

        public static ItemGroupButton discord(String url) {
            return link(Icon.of(DISCORD_ICON, 0, 0, 16, 16), "discord", url);
        }

        public static ItemGroupButton link(Icon icon, String name, String url) {
            return new ItemGroupButton(KS_TAB, icon, name, TABS, () -> {
                final var client = MinecraftClient.getInstance();
                var screen = client.currentScreen;
                client.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) Util.getOperatingSystem().open(url);
                    client.setScreen(screen);
                }, url, true));
            });
        }

        @Override
        public Identifier texture() {
            return this.texture;
        }

        @Override
        public Icon icon() {
            return icon;
        }

        @Override
        public Text tooltip() {
            return tooltip;
        }
    }
}