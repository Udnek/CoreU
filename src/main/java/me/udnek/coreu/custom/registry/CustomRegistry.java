package me.udnek.coreu.custom.registry;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface CustomRegistry<T extends Registrable> extends Registrable{
    @NotNull <V extends T>  V register(Plugin plugin, @NotNull V custom);
    @Nullable T get(@Nullable String id);
    @NotNull T get(int index);
    int getIndex(@NotNull T custom);
    default @NotNull T getOrException(String id){
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
