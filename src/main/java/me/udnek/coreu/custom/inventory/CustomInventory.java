package me.udnek.coreu.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@org.jspecify.annotations.NullMarked public  interface CustomInventory extends InventoryHolder{
    static boolean isCustom(Inventory inventory){
        return inventory.getHolder() instanceof CustomInventory;
    }
    static boolean isCustom(InventoryHolder holder){
        return holder instanceof CustomInventory;
    }
    static @Nullable CustomInventory get(Inventory inventory){
        if (inventory.getHolder() instanceof CustomInventory customInventory){
            return customInventory;
        } return null;
    }
    default void open(Player player) {player.openInventory(getInventory());}
    default void onPlayerClicksItem(InventoryClickEvent event){}
    default void afterPlayerClicksItem(InventoryClickEvent event){}
    default void onPlayerDragsItem(InventoryDragEvent event){}
    default void onPlayerClosesInventory(InventoryCloseEvent event){}
    default void onPlayerOpensInventory(InventoryOpenEvent event){}
    default boolean shouldAutoUpdateItems() { return true; }
    default void onHopperMoveFrom(InventoryMoveItemEvent event){}
    default void onHopperMoveInto(InventoryMoveItemEvent event){}
    default boolean isOpened(Player player){
        return get(player.getOpenInventory().getTopInventory()) == this;
    }
    default boolean isOpenedByAnyone() { return !getInventory().getViewers().isEmpty(); }
    default List<Player> getViewers(){
        return getInventory().getViewers().stream().map(human -> (Player) human).toList();
    }
}

