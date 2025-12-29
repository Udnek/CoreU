package me.udnek.coreu.custom.entitylike.block.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.entitylike.block.CustomBlockEntity;
import me.udnek.coreu.custom.entitylike.block.CustomBlockManager;
import me.udnek.coreu.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class LoadedCustomBlocksCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        commandSender.sendMessage("Loaded blocks:");
        for (CustomBlockEntity block : CustomBlockManager.getInstance().getAllLoaded()) {
            Location location = block.getReal().getLocation();
            commandSender.sendMessage(block +" " +block.getType().getId() + " " +
                    Utils.roundToTwoDigits(location.x()) + " " + Utils.roundToTwoDigits(location.y()) + Utils.roundToTwoDigits(location.z()) + " " + location.getWorld().getName());
        }
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
