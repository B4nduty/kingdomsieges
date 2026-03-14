package banduty.kingdomsieges.items;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.stoneycore.StoneyCore;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface KSItemGroups {
    ResourceLocation DISCORD_ICON = new ResourceLocation(StoneyCore.MOD_ID, "textures/gui/button/discord.png");
    ResourceLocation BACKGROUND = new ResourceLocation(StoneyCore.MOD_ID, "textures/gui/group_black.png");
    ResourceLocation TABS = new ResourceLocation(StoneyCore.MOD_ID, "textures/gui/tabs_black.png");

    private static ItemStack itemStack(ItemLike item) {
        return new ItemStack(item);
    }

    private static void items(CreativeModeTab.ItemDisplayParameters ctx, CreativeModeTab.Output stacks) {
        stacks.acceptAll(List.of(
                itemStack(KSItems.RAMROD),
                itemStack(KSItems.CANNON_MANUSCRIPT),
                itemStack(KSItems.BATTERING_RAM_MANUSCRIPT),
                itemStack(KSItems.RIBAULDEQUIN_MANUSCRIPT),
                itemStack(KSItems.MANGONEL_MANUSCRIPT),
                itemStack(KSItems.TREBUCHET_MANUSCRIPT),
                itemStack(KSItems.MANTLET_MANUSCRIPT)
        ));
    }

    private static void siegeEngines(CreativeModeTab.ItemDisplayParameters ctx, CreativeModeTab.Output stacks) {
        stacks.acceptAll(List.of(
                itemStack(KSItems.CANNON_SPAWNER),
                itemStack(KSItems.BATTERING_RAM_SPAWNER),
                itemStack(KSItems.RIBAULDEQUIN_SPAWNER),
                itemStack(KSItems.MANGONEL_SPAWNER),
                itemStack(KSItems.TREBUCHET_SPAWNER),
                itemStack(KSItems.MANTLET_SPAWNER)
        ));
    }

    ItemGroupTab KS_SIEGE_ENGINES_TAB = new ItemGroupTab(
            Icon.of(KSItems.CANNON_SPAWNER),
            Component.translatable("component.itemgroup.kingdomsieges.tab.ks_siege_engines").withStyle(ChatFormatting.WHITE),
            KSItemGroups::siegeEngines,
            TABS,
            true
    );

    ItemGroupTab KS_ITEMS_TAB = new ItemGroupTab(
            Icon.of(KSItems.RAMROD),
            Component.translatable("component.itemgroup.kingdomsieges.tab.ks_items").withStyle(ChatFormatting.WHITE),
            KSItemGroups::items,
            TABS,
            true
    );

    OwoItemGroup KS_TAB = OwoItemGroup
        .builder(new ResourceLocation(Kingdomsieges.MOD_ID, "ks_tab"), () -> Icon.of(new ResourceLocation(Kingdomsieges.MOD_ID,
                "icon.png"), 0, 0, 16, 16))
            .customTexture(BACKGROUND)
            .initializer(KSItemGroups::initializeGroup)
            .displaySingleTab()
            .build();

    private static void initializeGroup(OwoItemGroup group) {
        group.tabs.add(KS_SIEGE_ENGINES_TAB);
        group.tabs.add(KS_ITEMS_TAB);
        group.addButton(LinkButton.discord("https://discord.gg/AbtCqntN9S"));
    }
    
    static void registerItemGroups() {
        KS_TAB.initialize();
    }

    @Environment(EnvType.CLIENT)
    class LinkButton implements OwoItemGroup.ButtonDefinition {
        private final Icon icon;
        private final Component tooltip;
        private final ResourceLocation texture;

        public LinkButton(CreativeModeTab group, Icon icon, String name, ResourceLocation texture) {
            this.icon = icon;
            this.tooltip = OwoItemGroup.ButtonDefinition.tooltipFor(group, "button", name);
            this.texture = texture;
        }

        public static ItemGroupButton discord(String url) {
            return link(Icon.of(DISCORD_ICON, 0, 0, 16, 16), "discord", url);
        }

        public static ItemGroupButton link(Icon icon, String name, String url) {
            return new ItemGroupButton(KS_TAB, icon, name, TABS, () -> {
                final var client = Minecraft.getInstance();
                var screen = client.screen;
                client.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) Util.getPlatform().openUri(url);
                    client.setScreen(screen);
                }, url, true));
            });
        }

        @Override
        public ResourceLocation texture() {
            return this.texture;
        }

        @Override
        public Icon icon() {
            return icon;
        }

        @Override
        public Component tooltip() {
            return tooltip;
        }
    }
}