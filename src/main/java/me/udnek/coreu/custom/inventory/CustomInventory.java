package me.udnek.coreu.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomInventory extends InventoryHolder {
    static boolean isCustom(@NotNull Inventory inventory){
        return inventory.getHolder() instanceof CustomInventory;
    }
    static boolean isCustom(@NotNull InventoryHolder holder){
        return holder instanceof CustomInventory;
    }
    static @Nullable CustomInventory get(@NotNull Inventory inventory){
        if (inventory.getHolder() instanceof CustomInventory customInventory){
            return customInventory;
        } return null;
    }
    default void open(@NotNull Player player) {player.openInventory(getInventory());}
    default void onPlayerClicksItem(@NotNull InventoryClickEvent event){}
    default void afterPlayerClicksItem(@NotNull InventoryClickEvent event){}
    default void onPlayerDragsItem(@NotNull InventoryDragEvent event){}
    default void onPlayerClosesInventory(@NotNull InventoryCloseEvent event){}
    default void onPlayerOpensInventory(@NotNull InventoryOpenEvent event){}
    default boolean shouldAutoUpdateItems() { return true; }
    default boolean isOpened(@NotNull Player player){
        return get(player.getOpenInventory().getTopInventory()) == this;
    }
    default boolean isOpenedByAnyone() { return !getInventory().getViewers().isEmpty(); }
    default @NotNull List<Player> getViewers(){
        return getInventory().getViewers().stream().map(human -> (Player) human).toList();
    }
}

