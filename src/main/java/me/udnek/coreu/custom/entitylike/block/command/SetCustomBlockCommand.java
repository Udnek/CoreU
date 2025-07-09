package me.udnek.coreu.custom.entitylike.block.command;

import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetCustomBlockCommand implements TabExecutor, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return false;

        if (args.length != 1) return false;

        String id = args[0];

        CustomBlockType blockType = CustomRegistries.BLOCK_TYPE.get(id);
        if (blockType == null) return false;

        blockType.place(player.getLocation(), new CustomBlockPlaceContext(player, null, null));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length > 1) return List.of();

        List<String> ids = new ArrayList<>(CustomRegistries.BLOCK_TYPE.getIds());
        ids.removeIf(id -> !id.contains(args[0]));

        return ids;
    }
}
