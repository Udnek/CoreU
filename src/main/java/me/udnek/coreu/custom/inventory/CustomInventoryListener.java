package me.udnek.coreu.custom.inventory;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.util.SelfRegisteringListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryListener extends SelfRegisteringListener {

    public CustomInventoryListener(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerClicksInInventory(InventoryClickEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory customInventory){
            customInventory.onPlayerClicksItem(event);

            new BukkitRunnable() {
                @Override
                public void run() {
                    customInventory.afterPlayerClicksItem(event);
                }
            }.runTaskLater(CoreU.getInstance(), 1);
        }
    }
    @EventHandler
    public void onPlayerClosesInventory(InventoryCloseEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory customInventory){
            customInventory.onPlayerClosesInventory(event);
        }
    }

    @EventHandler
    public void onPlayerOpensInventory(InventoryOpenEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory customInventory){
            customInventory.onPlayerOpensInventory(event);
        }
    }

    @EventHandler
    public void onPlayerDragsItem(InventoryDragEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory customInventory){
            customInventory.onPlayerDragsItem(event);
        }
    }
}
