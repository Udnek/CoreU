package me.udnek.coreu.custom.hud;

import me.udnek.coreu.util.TickingTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CustomHudManager extends TickingTask {

    private static final HashMap<JavaPlugin, CustomHud> tickets = new HashMap<>();

    private static CustomHudManager instance;

    public static @NotNull CustomHudManager getInstance() {
        if (instance == null) instance = new CustomHudManager();
        return instance;
    }

    private CustomHudManager(){}

    public void addTicket(@NotNull JavaPlugin plugin, @NotNull CustomHud customHud){
        tickets.put(plugin, customHud);
    }

    public void removeTicket(@NotNull JavaPlugin plugin){
        tickets.remove(plugin);
    }

    @Override
    public int getDelay() {return 1;}

    @Override
    public void run() {
        if (tickets.isEmpty()) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Component text = Component.empty();

            for (CustomHud customHud : tickets.values()) {
                text =  text.append(customHud.getText(player));
            }

            player.sendActionBar(text);
        }
    }
}
