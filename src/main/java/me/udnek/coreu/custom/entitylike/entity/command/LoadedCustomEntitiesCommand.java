package me.udnek.coreu.custom.entitylike.entity.command;


import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.entitylike.entity.CustomEntity;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityManager;
import me.udnek.coreu.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@org.jspecify.annotations.NullMarked public class LoadedCustomEntitiesCommand implements BasicCommand{
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        sender.sendMessage("Loaded entities:");
        for (CustomEntity customEntity : CustomEntityManager.getInstance().getAllLoaded()) {
            Location location = customEntity.getReal().getLocation();
            sender.sendMessage(customEntity.getReal().getType() +" " +customEntity.getType().getId() + " " +
                    Utils.roundToTwoDigits(location.x()) + " " + Utils.roundToTwoDigits(location.y()) + Utils.roundToTwoDigits(location.z()) + " " + location.getWorld().getName());
        }
        return;
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
