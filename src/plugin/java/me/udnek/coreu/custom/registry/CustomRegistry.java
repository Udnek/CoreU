package me.udnek.coreu.custom.registry;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface CustomRegistry<T extends Registrable> extends Registrable{
    @NonNull <V extends T>  V register(Plugin plugin, @NonNull V custom);
    @Nullable T get(@Nullable String id);
    @NonNull T get(int index);
    int getIndex(@NonNull T custom);
    default @NonNull T getOrException(String id){
        @Nullable T item = get(id);
        Preconditions.checkArgument(item != null, "No such item in registry: " + id);
        return item;
    }
    boolean contains(@Nullable String id);
    Collection<String> getIds();
    Collection<T> getAllByPlugin(Plugin plugin);
    void getAll(Consumer<T> consumer);
    Collection<T> getAll();
}
