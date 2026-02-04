package me.udnek.coreu.mgu.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@org.jspecify.annotations.NullMarked public class MGUCommand implements BasicCommand{

    public List<String> getOptions(CommandSender commandSender, String[] args){
        if (args.length == 1){
            List<String> option = new ArrayList<>();
            for (MGUCommandType value : MGUCommandType.values()) option.add(value.name);
            return option;
        }
        MGUCommandType type = MGUCommandType.getType(args);
        if (type == null) return List.of();
        return type.getOptions(commandSender, args);
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        MGUCommandType commandType = MGUCommandType.getType(args);
        if (commandType == null) return;
        if (commandType.playerOnly && !(commandSender instanceof Player)) return;
        if (!commandType.testArgs(commandSender, args)) return;

        MGUCommandType.ExecutionResult execute = commandType.execute(commandSender, args);
        TextColor color = execute.type() == MGUCommandType.ExecutionResult.Type.SUCCESS ? NamedTextColor.GREEN : NamedTextColor.RED;
        commandSender.sendMessage(Component.text(execute.message()).color(color));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        List<String> options = getOptions(commandSourceStack.getSender(), args);
        if (args.length == 0) return options;
        return options.stream().filter(s1 -> s1.contains(args[args.length-1])).toList();
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
