package me.udnek.coreu.custom.registry;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@org.jspecify.annotations.NullMarked public  interface Registrable extends Keyed{
    void initialize(Plugin plugin);
    @MustBeInvokedByOverriders
    default void globalInitialization(){}
    String getId();

    @Override
    default NamespacedKey getKey(){
        return NamespacedKey.fromString(getId());
    }
}
