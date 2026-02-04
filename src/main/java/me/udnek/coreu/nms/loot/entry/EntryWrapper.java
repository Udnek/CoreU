package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.util.NmsConditioned;
import net.minecraft.world.level.storage.loot.entries.*;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface EntryWrapper extends NmsWrapper<LootPoolEntryContainer>, NmsConditioned{

    static EntryWrapper fromNms(LootPoolEntryContainer entry){
        if (entry.getType() == LootPoolEntries.LOOT_TABLE){
            return new NestedEntryWrapper((NestedLootTable) entry);
        } else if (entry instanceof LootPoolSingletonContainer singleton) {
            return new SingletonEntryWrapper(singleton);
        }
        return new CompositeEntryWrapper((CompositeEntryBase) entry);
    }

    void extractAllSingleton(Consumer<SingletonEntryWrapper> consumer);
}
