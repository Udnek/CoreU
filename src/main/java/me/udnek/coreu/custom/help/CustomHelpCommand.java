package me.udnek.coreu.custom.help;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class CustomHelpCommand implements BasicCommand{

    @UnknownNullability
    private static CustomHelpCommand instance;

    private final List<Component> lines = new ArrayList<>();

    private CustomHelpCommand(){
        String l = "-------------";
        lines.add(
                Component.text(l).color(NamedTextColor.YELLOW)
                        .append(Component.text(" Help ").color(NamedTextColor.WHITE))
                        .append(Component.text(l).color(NamedTextColor.YELLOW))
        );

    }
    public static CustomHelpCommand getInstance() {
        if (instance == null) instance = new CustomHelpCommand();
        return instance;
    }

    public void addLine(@Nullable Component component){
        if (component == null) component = Component.empty();
        lines.add(component);
    }

    public void trigger(CommandSender commandSender){
        lines.forEach(commandSender::sendMessage);
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        trigger(commandSourceStack.getSender());
    }
}














