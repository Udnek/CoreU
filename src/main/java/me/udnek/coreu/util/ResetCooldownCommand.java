package me.udnek.coreu.util;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

@org.jspecify.annotations.NullMarked public class ResetCooldownCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;
        ItemStack item = player.getEquipment().getItem(EquipmentSlot.HAND);
        player.setCooldown(item, 0);
    }

    @Override
    public @Nullable String permission() {
        return "coreu.admin";
    }
}
