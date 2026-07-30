package me.udnek.coreu.custom.attribute;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked public class ClearAttributeCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;
        for (@NonNull Attribute attribute : Registry.ATTRIBUTE.stream().toList()) {
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
