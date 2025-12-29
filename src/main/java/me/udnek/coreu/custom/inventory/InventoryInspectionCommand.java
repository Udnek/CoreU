package me.udnek.coreu.custom.inventory;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryInspectionCommand implements BasicCommand {
    static final List<Player> inspectingPlayers = new ArrayList<>();


    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;
        if (args.length > 0) return;
        if (!inspectingPlayers.isEmpty() && inspectingPlayers.contains(player)){
            inspectingPlayers.remove(player);
        }else {
            inspectingPlayers.add(player);
        }
    }

    @Override
    public @Nullable String permission() {
        return "coreu.admin";
    }
}