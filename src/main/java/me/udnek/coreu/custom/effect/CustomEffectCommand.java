package me.udnek.coreu.custom.effect;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomEffectCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        LivingEntity target;
        CommandSender commandSender = commandSourceStack.getSender();
        if (commandSender instanceof LivingEntity) target = (LivingEntity) commandSender;
        else if (Bukkit.getOnlinePlayers().size() == 1) {
            target = (LivingEntity) Bukkit.getOnlinePlayers().toArray()[0];
        } else {
            return;
        }
        if (args.length < 1) return;

        int duration = 10*20;
        if (args.length > 1) duration = Integer.parseInt(args[1]);
        int lvl = 0;
        if (args.length > 2) lvl = Integer.parseInt(args[2]);

        NamespacedKey id = NamespacedKey.fromString(args[0]);
        if (id == null) return;
        PotionEffectType effectType = Registry.EFFECT.get(id);
        if (effectType != null){
            target.addPotionEffect(new PotionEffect(effectType, duration, lvl));
            return;
        }
        CustomEffect customEffect = CustomRegistries.EFFECT.get(args[0]);
        if (customEffect != null){
            customEffect.apply(target, duration, lvl);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (args.length >= 4) return List.of();
        if (args.length == 3) return List.of("level");
        if (args.length == 2) return List.of("duration");
        List<String> ids = new ArrayList<>(Registry.EFFECT.stream().map(t -> t.key().asString()).toList());
        ids.addAll(CustomRegistries.EFFECT.getIds());
        if (args.length > 0) ids.removeIf(id -> !id.contains(args[0]));
        return ids;
    }


    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
