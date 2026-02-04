package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.util.NmsConditioned;
import net.minecraft.world.level.storage.loot.entries.*;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked
public interface EntryWrapper extends NmsWrapper<LootPoolEntryContainer>, NmsConditioned{

    static EntryWrapper fromNms(LootPoolEntryContainer entry){
        return switch (entry) {
            case NestedLootTable nested -> new NestedEntryWrapper(nested);
            case TagEntry tag -> new TagEntryWrapper(tag);
            case LootPoolSingletonContainer singleton -> new SingletonEntryWrapper(singleton);
            default -> new CompositeEntryWrapper((CompositeEntryBase) entry);
        };
    }

    void extractAllSingleton(Consumer<SingletonEntryWrapper> consumer);
}
