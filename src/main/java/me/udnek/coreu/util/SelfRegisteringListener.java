package me.udnek.coreu.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@org.jspecify.annotations.NullMarked public abstract class SelfRegisteringListener implements Listener{
    public SelfRegisteringListener(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
