package me.udnek.coreu.custom.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  interface SmartIntractableCustomInventory extends CustomInventory{
    boolean canPlaceItem(@Nullable ItemStack itemStack, int slot);
    boolean canTakeItem(@Nullable ItemStack itemStack, int slot);
    @Override
    default void onPlayerClicksItem(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (isPlaceAction(event) && !canPlaceItem(event.getCurrentItem(), slot)){
            event.setCancelled(true);
        } else if (isTakeAction(event) && !canTakeItem(event.getCurrentItem(), slot)) {
            event.setCancelled(true);
        }
    }

    @Override
    default void onPlayerDragsItem(InventoryDragEvent event) {
        Inventory upper = event.getView().getTopInventory();
        for (Integer rawSlot : event.getRawSlots()) {
            Inventory inventory = event.getView().getInventory(rawSlot);
            if (!upper.equals(inventory)) continue;
            ItemStack item = event.getView().getItem(rawSlot);
            if (!canPlaceItem(item, event.getView().convertSlot(rawSlot))){
                event.setCancelled(true);
                return;
            }
        }
    }

    default boolean isPlaceAction(InventoryClickEvent event){
        // UPPER
        if (event.getClickedInventory() == event.getView().getTopInventory()){
            return switch (event.getAction()) {
                case PLACE_ALL,
                     PLACE_SOME,
                     PLACE_ONE,

                     SWAP_WITH_CURSOR,
                     HOTBAR_SWAP,
                     COLLECT_TO_CURSOR,

                     PLACE_FROM_BUNDLE,
                     PLACE_ALL_INTO_BUNDLE,
                     PLACE_SOME_INTO_BUNDLE -> true;
                default -> false;
            };
        } else {
            return switch (event.getAction()) {
                case
                        SWAP_WITH_CURSOR,
                        HOTBAR_SWAP,
                        MOVE_TO_OTHER_INVENTORY,
                        COLLECT_TO_CURSOR
                        -> true;
                default -> false;
            };
        }
    }
    default boolean isTakeAction(InventoryClickEvent event){
        // UPPER
        if (event.getClickedInventory() == event.getView().getTopInventory()){
            return switch (event.getAction()) {
                case
                        PICKUP_ALL,
                        PICKUP_ONE,
                        PICKUP_HALF,
                        PICKUP_SOME,

                        SWAP_WITH_CURSOR,
                        HOTBAR_SWAP,
                        MOVE_TO_OTHER_INVENTORY,
                        COLLECT_TO_CURSOR,
                        DROP_ONE_SLOT,
                        DROP_ALL_SLOT,

                        PICKUP_FROM_BUNDLE,
                        PICKUP_ALL_INTO_BUNDLE,
                        PICKUP_SOME_INTO_BUNDLE
                        -> true;
                default -> false;
            };
        } else {
            return switch (event.getAction()) {
                case
                        SWAP_WITH_CURSOR,
                        HOTBAR_SWAP,
                        COLLECT_TO_CURSOR,
                        DROP_ONE_SLOT,
                        DROP_ALL_SLOT
                        -> true;
                default -> false;
            };
        }

    }

}
