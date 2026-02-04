package me.udnek.coreu.custom.entitylike.block.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.entitylike.block.CustomBlockEntity;
import me.udnek.coreu.custom.entitylike.block.CustomBlockManager;
import me.udnek.coreu.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@org.jspecify.annotations.NullMarked public class LoadedCustomBlocksCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
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
