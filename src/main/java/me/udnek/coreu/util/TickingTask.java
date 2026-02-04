package me.udnek.coreu.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public abstract class TickingTask implements Runnable{

    protected @Nullable BukkitTask task;

    public void start(JavaPlugin plugin){
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, getDelay());
    }

    public void stop(){
        if (task == null) return;
        if (task.isCancelled()) return;
        task.cancel();
    }

    public abstract @Positive int getDelay();

}
