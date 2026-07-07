package me.udnek.coreu;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CoreU {

    private static @UnknownNullability Plugin instance;

    public static void setPluginInstance(Plugin plugin){
        instance = plugin;
    }

    public static Plugin getPlugin(){
        return instance;
    }

    public static Key getKey(String value) {
        return new NamespacedKey(getPlugin(), value);
    }
}
