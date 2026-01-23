package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.util.NmsConditioned;
import net.minecraft.world.level.storage.loot.entries.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EntryWrapper extends NmsWrapper<@NotNull LootPoolEntryContainer>, NmsConditioned {

    static @NotNull EntryWrapper fromNms(@NotNull LootPoolEntryContainer entry){
        if (entry.getType() == LootPoolEntries.LOOT_TABLE){
            return new NestedEntryWrapper((NestedLootTable) entry);
        } else if (entry instanceof LootPoolSingletonContainer singleton) {
            return new SingletonEntryWrapper(singleton);
        }
        return new CompositeEntryWrapper((CompositeEntryBase) entry);
    }

    void extractAllSingleton(@NotNull Consumer<SingletonEntryWrapper> consumer);
}
