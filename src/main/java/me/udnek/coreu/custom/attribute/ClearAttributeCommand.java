package me.udnek.coreu.custom.attribute;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ClearAttributeCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;
        for (@NotNull Attribute attribute : Registry.ATTRIBUTE.stream().toList()) {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance == null) continue;
            for (AttributeModifier modifier : attributeInstance.getModifiers()) {
                attributeInstance.removeModifier(modifier);
            }
        }
    }

    @Override
    public @Nullable String permission() {
        return "coreu.admin";
    }
}
