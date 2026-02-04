package me.udnek.coreu.custom.entitylike.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.entitylike.EntityLikeType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface CustomBlockType extends ComponentHolder<CustomBlockType>, EntityLikeType<TileState>{

    NamespacedKey PDC_KEY = new NamespacedKey(CoreU.getInstance(), "custom_block_type");

    static @Nullable CustomBlockEntity getTicking(Block block){
        BlockState state = block.getState();
        if (state instanceof TileState tileState){
            return CustomBlockManager.getInstance().getTicking(tileState);
        }
        return null;
    }

    static @Nullable String getId(Block block){
        BlockState state = block.getState();
        if (state instanceof TileState tileState) return getId(tileState);
        return null;
    }

    static @Nullable String getId(TileState blockState){
        return blockState.getPersistentDataContainer().get(PDC_KEY, PersistentDataType.STRING);
    }

    static @Nullable String getId(Location location){
        return getId(location.getBlock());
    }

    static @Nullable CustomBlockType get(Block block){
        return CustomRegistries.BLOCK_TYPE.get(getId(block));
    }
    static @Nullable CustomBlockType get(TileState blockState){
        return CustomRegistries.BLOCK_TYPE.get(getId(blockState));
    }
    static @Nullable CustomBlockType get(String id){
        return CustomRegistries.BLOCK_TYPE.get(id);
    }

    static void consumeIfCustom(Block block, Consumer<CustomBlockType> consumer){
        CustomBlockType blockType = get(block);
        if (blockType != null) consumer.accept(blockType);
    }

    /////////////////////

    @Nullable CustomItem getItem();

    @Nullable BlockState getFakeState();

    float getCustomBreakProgress(Player player, Block block);

    void onPlayerFall(Player player, Location location, int particlesCount);

    void onDamage(BlockDamageEvent event);

    void place(Location location, CustomBlockPlaceContext context);
    void destroy(Location location);
    void onDestroy(BlockFadeEvent event);
    void onDestroy(BlockBurnEvent event);
    void onTouchedByExplosion(EntityExplodeEvent event, Block block);
    void onDestroy(BlockDestroyEvent event);
    void onDestroy(BlockBreakEvent event);
    void onTouchedByExplosion(BlockExplodeEvent event, Block block);

    void customBreakTickProgress(Block block, Player player, float progress);

    boolean doCustomBreakTimeAndAnimation();
}
