package me.udnek.coreu.custom.sound;

import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  interface CustomSound extends Registrable, ComponentHolder<CustomSound>, Translatable{
    void play(@Nullable Location location, @Nullable Player player, SoundCategory category, float volume, float pitch);
    void play(@Nullable Location location, @Nullable Player player, float volume, float pitch);
    void play(@Nullable Location location, @Nullable Player player, float volume);
    void play(@Nullable Location location, @Nullable Player player);
    default void play(Player player){play(null, player);}
    default void play(Location location){
        play(location, null);
    }
    void stop(Player player);
    void stop(Player player, SoundCategory soundCategory);
    void stopInHearableRadius(Location location);
    void stopInRadius(Location location, float radius);
}
