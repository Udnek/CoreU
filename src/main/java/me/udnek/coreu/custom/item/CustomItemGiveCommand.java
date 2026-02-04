package me.udnek.coreu.custom.item;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@org.jspecify.annotations.NullMarked public class CustomItemGiveCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length != 1) return;

        CustomItem customItem = CustomItem.get(args[0]);
        if (customItem == null) return;

        ItemStack itemStack = customItem.getItem();

        player.getInventory().addItem(itemStack);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length > 1) return List.of();

        String arg = args.length == 0 ? "" : args[0];
        List<String> ids = new ArrayList<>(CustomRegistries.ITEM.getIds());
        ids.removeIf(id -> !id.contains(arg));

        return ids;
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
