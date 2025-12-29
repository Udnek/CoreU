package me.udnek.coreu.custom.entitylike.entity.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SummonCustomEntityCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        if (args.length != 1) return;
        CustomEntityType entityType = CustomEntityType.get(args[0]);
        if (entityType == null) return;

        Location location;
        CommandSender sender = commandSourceStack.getSender();
        if (sender instanceof Player player) {
            location = player.getLocation();
        } else if (sender instanceof BlockCommandSender blockCommandSender) {
            location = blockCommandSender.getBlock().getLocation().toCenterLocation().add(0, 0.5, 0);
        } else {
            return;
        }

        entityType.spawn(location);
        return;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length > 1) return List.of();
        return new ArrayList<>(CustomRegistries.ENTITY_TYPE.getIds());
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
