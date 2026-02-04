package me.udnek.coreu.custom.inventory;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class InventoryInspectionCommand implements BasicCommand{
    static final List<Player> inspectingPlayers = new ArrayList<>();


    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
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