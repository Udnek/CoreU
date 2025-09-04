package me.udnek.coreu.custom.entitylike.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.EntityLikeManager;
import me.udnek.coreu.nms.Nms;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomBlockManager extends EntityLikeManager<TileState, CustomBlockType, CustomBlockEntity> implements Listener {

    public static final int MAX_CUSTOM_BLOCK_PACKETS_PER_TICK = 50;

    private static CustomBlockManager instance;

    private final HashMap<Block, BreakData> breaking = new HashMap<>();

    public static CustomBlockManager getInstance() {
        if (instance == null) {
            instance = new CustomBlockManager();
            Bukkit.getPluginManager().registerEvents(instance, CoreU.getInstance());
        }
        return instance;
    }

    private CustomBlockManager(){}

    @Override
    protected boolean equals(@NotNull TileState r1, @NotNull TileState r2) {
        return r1.equals(r2);
    }

    public void loadChunk(@NotNull Chunk chunk){
        for (BlockState blockState : chunk.getTileEntities()) {
            CustomBlockType customBlockType = CustomBlockType.get((TileState) blockState);
            if (customBlockType != null) loadAny(customBlockType, (TileState) blockState);
        }
    }

    // TODO FIX SERVER CALLS
    public void unloadChunk(@NotNull Chunk chunk){
        for (BlockState blockState : chunk.getTileEntities()) {
            CustomBlockType customBlockType = CustomBlockType.get((TileState) blockState);
            if (customBlockType != null) unloadAny(customBlockType, (TileState) blockState);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event){
        CustomBlockType blockType = CustomBlockType.get(event.getBlock());
        if (blockType == null) return;
        if (!blockType.doCustomBreakTimeAndAnimation()) return;
        BreakData breakData = breaking.get(event.getBlock());
        if (breakData == null){
            breakData = new BreakData(blockType, event.getPlayer());
            breaking.put(event.getBlock(), breakData);
        } else {
            breakData.progress.put(event.getPlayer(), 0f);
        }
        event.setCancelled(true);
        blockType.onDamage(event);
    }

    @EventHandler
    public void onBlockAbortDamage(BlockDamageAbortEvent event){
        CustomBlockType blockType = CustomBlockType.get(event.getBlock());
        if (blockType == null) return;
        BreakData breakData = breaking.get(event.getBlock());
        if (breakData == null) return;
        breakData.progress.remove(event.getPlayer());
        if (breakData.progress.isEmpty()) breaking.remove(event.getBlock());
    }


    @Override
    public void run() {
        super.run();
        if (breaking.isEmpty()) return;
        List<Block> toRemove = new ArrayList<>();
        for (Map.Entry<Block, BreakData> entry : breaking.entrySet()) {
            BreakData breakData = entry.getValue();
            Block block = entry.getKey();

            Player bestPlayer = null;
            float bestProgress = 0;
            for (Map.Entry<Player, Float> progressEntry : breakData.progress.entrySet()) {
                Player player = progressEntry.getKey();
                float progress = Math.clamp(
                        progressEntry.getValue() +
                                breakData.customBlock.getCustomBreakProgress(player, block), 0, 1);
                progressEntry.setValue(progress);
                if (progress == 1){
                    toRemove.add(block);
                    player.breakBlock(block);
                    bestPlayer = null;
                    break;
                } else {
                    if (progress > bestProgress){
                        bestPlayer = player;
                        bestProgress = progress;
                    }
                }
            }

            if (bestPlayer != null){
                breakData.customBlock.customBreakTickProgress(block, bestPlayer, bestProgress);
            }
        }
        toRemove.forEach(breaking::remove);
    }

    public static class BreakData {
        private final @NotNull CustomBlockType customBlock;
        private final HashMap<Player, Float> progress = new HashMap<>();

        public BreakData(@NotNull CustomBlockType customBlock, @NotNull Player player) {
            this.customBlock = customBlock;
            progress.put(player, 0f);
        }
    }

    // EVENTS

    @EventHandler
    public void onDestroy(BlockDestroyEvent event){
        CustomBlockType.consumeIfCustom(event.getBlock(), block -> block.onDestroy(event));
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        CustomBlockType.consumeIfCustom(event.getBlock(), block -> block.onDestroy(event));
    }
    @EventHandler
    public void onExplode(BlockExplodeEvent event){
        System.out.println(event.getYield());
        for (Block block : event.blockList()) {
            CustomBlockType.consumeIfCustom(block, type -> type.onTouchedByExplosion(event, block));
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        for (Block block : event.blockList()) {
            CustomBlockType.consumeIfCustom(block, type -> type.onTouchedByExplosion(event, block));
        }
    }
    @EventHandler
    public void onFade(BlockFadeEvent event){
        CustomBlockType.consumeIfCustom(event.getBlock(), block -> block.onDestroy(event));
    }
    @EventHandler
    public void onBurn(BlockBurnEvent event){
        CustomBlockType.consumeIfCustom(event.getBlock(), block -> block.onDestroy(event));
    }

    @EventHandler
    public void onHopperInteract(HopperInventorySearchEvent event){
        CustomBlockType.consumeIfCustom(event.getSearchBlock(), block ->
                block.getComponents().getOrDefault(CustomComponentType.HOPPER_INTERACTING_BLOCK).onHopperInteract(block, event)
        );
    }

    @EventHandler
    public void onMiddleClick(PlayerPickBlockEvent event){
        CustomBlockType.consumeIfCustom(event.getBlock(),
                type -> type.getComponents().getOrDefault(CustomComponentType.MIDDLE_CLICKABLE_BLOCK)
                        .onMiddleClick(type, event));
    }

    @EventHandler
    public void onInventoryItemMove(InventoryMoveItemEvent event){
        Location destination = event.getDestination().getLocation();
        Location source = event.getSource().getLocation();
        if (source != null){
            CustomBlockType.consumeIfCustom(source.getBlock(), block ->
                    block.getComponents().getOrDefault(CustomComponentType.HOPPER_INTERACTING_BLOCK).onItemMoveFrom(block, event)
            );
        }
        if (destination != null){
            CustomBlockType.consumeIfCustom(destination.getBlock(), block ->
                    block.getComponents().getOrDefault(CustomComponentType.HOPPER_INTERACTING_BLOCK).onItemMoveInto(block, event)
            );
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        CustomBlockType customBlockType = CustomBlockType.get(event.getClickedBlock());
        if (customBlockType == null) return;
        customBlockType.getComponents().getOrDefault(CustomComponentType.RIGHT_CLICKABLE_BLOCK).onRightClick(customBlockType, event);
    }

    @EventHandler
    public void onPlayerChunkLoad(@NotNull PlayerChunkLoadEvent event){
        List<BlockState> blocksToSendPackets = new ArrayList<>();
        for (BlockState blockState : event.getChunk().getTileEntities()) {
            CustomBlockType customBlockType = CustomBlockType.get((TileState) blockState);
            if (customBlockType == null) continue;
            if (customBlockType.getFakeState() == null) continue;
            blocksToSendPackets.add(blockState);
        }
        if (blocksToSendPackets.isEmpty()) return;
        new BukkitRunnable(){
            int packetId = 0;
            @Override
            public void run() {
                for (int i = packetId; i < blocksToSendPackets.size(); i++) {
                    BlockState blockState = blocksToSendPackets.get(i);
                    Nms.get().sendBlockUpdatePacket(event.getPlayer(), blockState);
                }
                packetId += MAX_CUSTOM_BLOCK_PACKETS_PER_TICK;
                if (packetId > blocksToSendPackets.size()-1) cancel();
            }
        }.runTaskTimer(CoreU.getInstance(), 8, 1);
    }
    @EventHandler
    public void onLoad(ChunkLoadEvent event){loadChunk(event.getChunk());}
    @EventHandler
    public void onUnload(ChunkUnloadEvent event){unloadChunk(event.getChunk());}
    @EventHandler
    public void onReload(ServerLoadEvent event){
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) return;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                loadChunk(loadedChunk);
            }
        }
    }
}
