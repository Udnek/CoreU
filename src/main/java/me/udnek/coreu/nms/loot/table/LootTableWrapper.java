package me.udnek.coreu.nms.loot.table;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.pool.PoolWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.function.Consumer;

public interface LootTableWrapper extends NmsWrapper<@NotNull LootTable>, NmsFunctioned {
    void addPool(@NotNull PoolWrapper container);
    @NotNull PoolWrapper getPool(int n);
    void removePool(int n);
    @NotNull List<PoolWrapper> getPools();
    @NotNull LootTableWrapper copy();

    void extractItems(@NotNull LootInfo base, @NotNull Consumer<Pair<ItemStack, LootInfo>> consumer);
    default void extractItems(@NotNull Consumer<Pair<ItemStack, LootInfo>> consumer){
        extractItems(new LootInfo(1, List.of()), consumer);
    }
}
