package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import me.udnek.coreu.util.Reflex;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public interface SingletonEntryWrapper extends EntryWrapper, NmsFunctioned {
    void setWeight(int n);
    int getWeight();
    void setQuality(int n);
    int getQuality();

    void extractItems(LootInfo baseInfo, Consumer<Pair<ItemStack, LootInfo>> consumer);
}
