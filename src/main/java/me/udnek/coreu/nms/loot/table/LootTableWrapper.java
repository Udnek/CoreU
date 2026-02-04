package me.udnek.coreu.nms.loot.table;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.pool.PoolWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface LootTableWrapper extends NmsWrapper<LootTable>, NmsFunctioned{
    void addPool(PoolWrapper container);
    PoolWrapper getPool(int n);
    void removePool(int n);
    List<PoolWrapper> getPools();
    LootTableWrapper copy();

    void extractItems(LootInfo base, Consumer<Pair<ItemStack, LootInfo>> consumer);
    default void extractItems(Consumer<Pair<ItemStack, LootInfo>> consumer){
        extractItems(new LootInfo(1, List.of()), consumer);
    }
}
