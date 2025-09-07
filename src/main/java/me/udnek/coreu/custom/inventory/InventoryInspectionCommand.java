package me.udnek.coreu.custom.inventory;

import me.udnek.coreu.util.SelfRegisteringListener;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InventoryInspectionCommand extends SelfRegisteringListener implements CommandExecutor {
    private final List<Player> inspectingPlayers = new ArrayList<>();

    public InventoryInspectionCommand(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player player)) return false;
        if (args.length > 0) return false;
        if (!inspectingPlayers.isEmpty() && inspectingPlayers.contains(player)){
            inspectingPlayers.remove(player);
        }else {
            inspectingPlayers.add(player);
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!inspectingPlayers.contains(player)) return;
        player.sendMessage(Component.text(event.getSlot()));
    }

}