package me.udnek.coreu.util;

import me.udnek.coreu.CoreU;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class FakeBlock {
    private static final HashMap<Block, FakeBlock> fakes = new HashMap<>();

    private @Nullable BukkitRunnable task;

    public void show(@NotNull List<@NotNull Player> players, @NotNull Location location, @NotNull BlockData blockData, long duration) {
        Block block = location.getBlock();
        for (Player player : players) {
            player.sendBlockChange(block.getLocation(), blockData);
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
            }
        };
        task.runTaskLater(CoreU.getInstance(), duration);

        FakeBlock oldFake = fakes.get(block);
        if (oldFake != null && oldFake.task != null) {
            oldFake.task.cancel();
        }
        fakes.put(block, this);
    }
}
