package me.udnek.coreu.custom.registry;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked public  interface CustomRegistry<T extends Registrable> extends Registrable{
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
    List<String> getIds();
    List<T> getAllByPlugin(Plugin plugin);
    void getAll(Consumer<T> consumer);
    List<T> getAll();
}
