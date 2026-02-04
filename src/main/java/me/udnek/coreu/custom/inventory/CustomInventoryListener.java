package me.udnek.coreu.custom.inventory;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.util.SelfRegisteringListener;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@org.jspecify.annotations.NullMarked public class CustomInventoryListener extends SelfRegisteringListener{

    public CustomInventoryListener(JavaPlugin plugin) {
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
            }.runTask(CoreU.getInstance());
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!InventoryInspectionCommand.inspectingPlayers.contains(player)) return;
        player.sendMessage(Component.text(event.getSlot()));
    }
}
