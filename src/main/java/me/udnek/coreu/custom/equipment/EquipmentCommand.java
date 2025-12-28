package me.udnek.coreu.custom.equipment;

import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EquipmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)){
            return false;
        }
        PlayerEquipment data = PlayerEquipmentManager.getInstance().getData(player);
        data.getEquipment(new PlayerEquipment.EquipmentConsumer() {
            @Override
            public void accept(@NotNull BaseUniversalSlot slot, @NotNull CustomItem customItem) {
                sender.sendMessage(slot + ": " + customItem.getId());
            }
        });
        return true;
    }
}
