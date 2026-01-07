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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface CustomBlockType extends ComponentHolder<CustomBlockType>, EntityLikeType<TileState> {

    NamespacedKey PDC_KEY = new NamespacedKey(CoreU.getInstance(), "custom_block_type");

    static @Nullable CustomBlockEntity getTicking(@NotNull Block block){
        BlockState state = block.getState();
        if (state instanceof TileState tileState){
            return CustomBlockManager.getInstance().getTicking(tileState);
        }
        return null;
    }

    static @Nullable String getId(@NotNull Block block){
        BlockState state = block.getState();
        if (state instanceof TileState tileState) return getId(tileState);
        return null;
    }

    static @Nullable String getId(@NotNull TileState blockState){
        return blockState.getPersistentDataContainer().get(PDC_KEY, PersistentDataType.STRING);
    }

    static @Nullable String getId(@NotNull Location location){
        return getId(location.getBlock());
    }

    static @Nullable CustomBlockType get(@NotNull Block block){
        return CustomRegistries.BLOCK_TYPE.get(getId(block));
    }
    static @Nullable CustomBlockType get(@NotNull TileState blockState){
        return CustomRegistries.BLOCK_TYPE.get(getId(blockState));
    }
    static @Nullable CustomBlockType get(@NotNull String id){
        return CustomRegistries.BLOCK_TYPE.get(id);
    }

    static void consumeIfCustom(@NotNull Block block, @NotNull Consumer<CustomBlockType> consumer){
        CustomBlockType blockType = get(block);
        if (blockType != null) consumer.accept(blockType);
    }

    /////////////////////

    @Nullable CustomItem getItem();

    @Nullable BlockState getFakeState();

    float getCustomBreakProgress(@NotNull Player player, @NotNull Block block);

    void onPlayerFall(@NotNull Player player, @NotNull Location location, int particlesCount);

    void onDamage(@NotNull BlockDamageEvent event);

    void place(@NotNull Location location, @NotNull CustomBlockPlaceContext context);
    void destroy(@NotNull Location location);
    void onDestroy(@NotNull BlockFadeEvent event);
    void onDestroy(@NotNull BlockBurnEvent event);
    void onTouchedByExplosion(@NotNull EntityExplodeEvent event, @NotNull Block block);
    void onDestroy(@NotNull BlockDestroyEvent event);
    void onDestroy(@NotNull BlockBreakEvent event);
    void onTouchedByExplosion(@NotNull BlockExplodeEvent event, @NotNull Block block);

    void customBreakTickProgress(@NotNull Block block, @NotNull Player player, float progress);

    boolean doCustomBreakTimeAndAnimation();
}
