package me.udnek.coreu.nms.loot.entry;

import com.google.common.base.Preconditions;
import me.udnek.coreu.nms.loot.pool.PoolWrapper;
import me.udnek.coreu.nms.loot.util.NmsConditioned;
import net.minecraft.world.level.storage.loot.entries.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked
public interface EntryWrapper extends NmsConditioned{

    static EntryWrapper fromNms(@Nullable PoolWrapper parent, LootPoolEntryContainer entry){
        return switch (entry) {
            case NestedLootTable nested -> new NestedEntryWrapper(parent, nested);
            case TagEntry tag -> new TagEntryWrapper(parent, tag);
            case LootPoolSingletonContainer singleton -> new SingletonEntryWrapperImpl(parent, singleton);
            default -> new CompositeEntryWrapper(parent, (CompositeEntryBase) entry);
        };
    }

    @Nullable PoolWrapper getPoolParent();

    default int getIndexInParent(){
        PoolWrapper parent = getPoolParent();
        Preconditions.checkArgument(parent != null);
        List<EntryWrapper> entries = parent.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getNms() == getNms()){
                return i;
            }
        }
        throw new RuntimeException(String.format("Could not index entry (%s) in parent (%s)", getNms(), parent.getNms()));
    }

    LootPoolEntryContainer getNms();

    void extractAllSingleton(Consumer<SingletonEntryWrapper> consumer);
}
