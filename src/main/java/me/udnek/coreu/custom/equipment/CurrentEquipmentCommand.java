package me.udnek.coreu.custom.equipment;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class CurrentEquipmentCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
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
