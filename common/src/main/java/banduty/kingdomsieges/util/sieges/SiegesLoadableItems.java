package banduty.kingdomsieges.util.sieges;

import net.minecraft.world.item.Item;

public record SiegesLoadableItems(
        Item item,
        int amount,
        boolean consumesItem,
        boolean damagesItem
) {
}