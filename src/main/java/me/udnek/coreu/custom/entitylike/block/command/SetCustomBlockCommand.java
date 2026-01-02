package me.udnek.coreu.custom.entitylike.block.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SetCustomBlockCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;

        if (args.length != 1) return;

        String id = args[0];

        CustomBlockType blockType = CustomRegistries.BLOCK_TYPE.get(id);
        if (blockType == null) return;

        blockType.place(player.getLocation(), new CustomBlockPlaceContext(player, null, null));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (args.length > 1) return List.of();

        List<String> ids = new ArrayList<>(CustomRegistries.BLOCK_TYPE.getIds());
        ids.removeIf(id -> !id.contains(args[0]));

        return ids;
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
