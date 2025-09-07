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
    private final @NotNull Location location;
    private final @NotNull BlockData blockData;
    private final long duration;
    private final @NotNull List<Player> players;


    public static void show(@NotNull Location location, @NotNull BlockData blockData, @NotNull List<@NotNull Player> players, long duration) {
        new FakeBlock(location, blockData, players, duration).run();
    }
    public static void stop(@NotNull Location location){
        FakeBlock fakeBlock = fakes.get(location.getBlock());
        if (fakeBlock == null) return;
        fakeBlock.stop(true);
    }

    private FakeBlock(@NotNull Location location, @NotNull BlockData blockData, @NotNull List<@NotNull Player> players, long duration){
        this.players = players;
        this.location = location.clone();
        this.blockData = blockData;
        this.duration = duration;
    }

    private void stop(boolean sendReal){
        if (task != null) task.cancel();
        fakes.remove(location.getBlock());
        if (!sendReal) return;
        BlockData blockData = location.getBlock().getBlockData();
        for (Player player : players) {
            player.sendBlockChange(location, blockData);
        }
    }

    private void run(){
        Block block = location.getBlock();
        for (Player player : players) {
            player.sendBlockChange(block.getLocation(), blockData);
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                stop(true);
            }
        };
        task.runTaskLater(CoreU.getInstance(), duration);

        FakeBlock oldFake = fakes.get(block);
        if (oldFake != null) oldFake.stop(false);
        fakes.put(block, this);
    }
}
