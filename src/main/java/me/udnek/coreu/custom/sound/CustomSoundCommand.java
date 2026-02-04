package me.udnek.coreu.custom.sound;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@org.jspecify.annotations.NullMarked public class CustomSoundCommand implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (!(commandSender instanceof Player player)) return;

        if (args.length != 1) return;
        CustomSound customSound = CustomRegistries.SOUND.get(args[0]);
        if (customSound == null) return;

        customSound.play(player);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length > 1) return List.of();
        List<String> ids = new ArrayList<>(CustomRegistries.SOUND.getIds());
        ids.removeIf(id -> !id.contains(args[0]));
        return ids;
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
