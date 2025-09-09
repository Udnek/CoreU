package me.udnek.coreu.custom.sound;

import com.google.common.base.Preconditions;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConstructableCustomSound extends AbstractRegistrableComponentable<CustomSound> implements CustomSound {

    private final String rawId;
    public final @NotNull SoundCategory category;
    protected @NotNull List<String> filePaths;
    public final float volume;
    public final float pitch;

    public ConstructableCustomSound(
            @NotNull String rawId,
            @NotNull SoundCategory category,
            @NotNull TranslatableThing translations,
            @NotNull List<String> rawPaths,
            float volume,
            float pitch
    ){
        this.rawId = rawId;
        this.category = category;
        getComponents().set(translations);
        this.filePaths = rawPaths;
        this.volume = volume;
        this.pitch = pitch;
    }

    public ConstructableCustomSound(@NotNull String rawPath, @NotNull SoundCategory category, @NotNull TranslatableThing translations, float volume, float pitch){
        this(rawPath, category, translations, List.of(rawPath), volume, pitch);
    }

    public ConstructableCustomSound(@NotNull String rawPath, @NotNull SoundCategory category, @NotNull TranslatableThing translations, float volume){
        this(rawPath, category, translations, volume, 1f);
    }

    public ConstructableCustomSound(@NotNull String rawPath, @NotNull SoundCategory category, @NotNull TranslatableThing translations){
        this(rawPath, category, translations,1F);
    }


    public @NotNull List<String> getFilePaths() {return filePaths;}

    @Override
    public void initialize(@NotNull Plugin plugin) {
        super.initialize(plugin);
        filePaths = filePaths.stream().map(path -> key().namespace() + ":" + path).toList();
    }

    @Override
    public @NotNull String translationKey() {return "subtitles."+key().namespace()+"."+key().value();}

    @Override
    public @NotNull String getRawId() {return rawId;}

    public @NotNull String getSoundName(){return getId();}

    @Override
    public void play(@Nullable Location location, @Nullable Player player) {
        play(location, player, category, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, float volume) {
        play(location, player, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, float volume, float pitch) {
        play(location, player, category, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, @NotNull SoundCategory category, float volume, float pitch) {
        Preconditions.checkArgument(player != null || location != null, "Both location and player can not be null");
        if (player == null){
            location.getWorld().playSound(location, getSoundName(), category, volume, pitch);
        } else if (location != null) {
            player.playSound(location, getSoundName(), category, volume, pitch);
        } else {
            player.playSound(player, getSoundName(), category, volume, pitch);
        }
    }

    @Override
    public void stop(@NotNull Player player) {
        player.stopSound(getSoundName(), category);
    }

    @Override
    public void stop(@NotNull Player player, @NotNull SoundCategory soundCategory) {
        player.stopSound(getSoundName(), soundCategory);
    }

    @Override
    public void stopInHearableRadius(@NotNull Location location) {
        stopInRadius(location, volume*16);
    }

    @Override
    public void stopInRadius(@NotNull Location location, float radius) {
        location.getWorld().getNearbyPlayers(location, radius).forEach(this::stop);
    }
}











