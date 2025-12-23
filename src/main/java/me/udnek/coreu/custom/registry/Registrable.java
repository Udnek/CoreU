package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.component.ComponentHolder;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public interface Registrable extends Keyed {
    void initialize(@NotNull Plugin plugin);
    @MustBeInvokedByOverriders
    default void afterInitialization(){}
    @NotNull String getId();

    @Override
    default @NotNull NamespacedKey getKey(){
        return NamespacedKey.fromString(getId());
    }
}
