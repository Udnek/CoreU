package me.udnek.coreu.custom.equipment;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

@org.jspecify.annotations.NullMarked public class CurrentEquipmentCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)){
            return;
        }
        PlayerEquipment data = PlayerEquipmentManager.getInstance().getData(player);
        data.getEquipment((slot, customItem) -> {
            sender.sendMessage(slot + ": " + customItem.getId());
        });
    }

    @Override
    public @Nullable String permission() {
        return "coreu.admin";
    }
}
