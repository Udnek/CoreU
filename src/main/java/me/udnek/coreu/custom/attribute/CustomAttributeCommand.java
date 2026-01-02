package me.udnek.coreu.custom.attribute;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomAttributeCommand implements BasicCommand {


    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof LivingEntity living)) return;
        if (args.length == 0 || args[0].equals("all")){
            for (CustomAttribute attribute : CustomRegistries.ATTRIBUTE.getAll()) {
                commandSender.sendMessage(attribute.getId() +": "+ attribute.calculate(living));
            }
        } else {
            CustomAttribute attribute = CustomRegistries.ATTRIBUTE.get(args[0]);
            if (attribute == null) return;
            commandSender.sendMessage(attribute.getId() +": "+  attribute.calculate(living));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        List<String> ids = new ArrayList<>(CustomRegistries.ATTRIBUTE.getIds());
        if (args.length > 0) ids.removeIf(id -> !id.contains(args[0]));
        ids.add("all");
        return ids;
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
