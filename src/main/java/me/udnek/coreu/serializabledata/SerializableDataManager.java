package me.udnek.coreu.serializabledata;

import me.udnek.coreu.CoreU;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SerializableDataManager {

    private static FileConfiguration config;
    private static final String PLUGIN_PATH = "plugin";
    private static final String PLAYER_PATH = "player";

    ///////////////////////////////////////////////////////////////////////////
    // READING
    ///////////////////////////////////////////////////////////////////////////
    public static <T extends SerializableData> @NotNull T read(@NotNull T data, @NotNull Plugin plugin, @NotNull Player player){
        String readRaw = readRaw(getPath(data, plugin, player));
        data.deserialize(readRaw);
        return data;
    }
    public static <T extends SerializableData> @NotNull T read(@NotNull T data, @NotNull Plugin plugin){
        String readRaw = readRaw(getPath(data, plugin));
        data.deserialize(readRaw);
        return data;
    }
    private static @Nullable String readRaw(@NotNull String path){
        loadConfig();
        return config.getString(path);
    }
    ///////////////////////////////////////////////////////////////////////////
    // WRITING
    ///////////////////////////////////////////////////////////////////////////
    public static void write(@NotNull SerializableData data, @NotNull Plugin plugin, @NotNull Player player){
        writeRaw(data, getPath(data, plugin, player));
    }
    public static void write(@NotNull SerializableData data, @NotNull Plugin plugin){
        writeRaw(data, getPath(data, plugin));
    }
    private static void writeRaw(@NotNull SerializableData serializableData, @NotNull String path){
        loadConfig();
        config.set(path, serializableData.serialize());
        saveConfig();
    }
    ///////////////////////////////////////////////////////////////////////////
    // INITIAL
    ///////////////////////////////////////////////////////////////////////////
    private static @NotNull String getPath(@NotNull SerializableData data, @NotNull Plugin plugin, @NotNull Player player){
        return toStringPath(
                PLAYER_PATH,
                player.getUniqueId().toString(),
                plugin.getName() + ":" + data.getDataName()
        );
    }
    private static @NotNull String getPath(@NotNull SerializableData data, @NotNull Plugin plugin){
        return toStringPath(
                PLUGIN_PATH,
                plugin.getName(),
                data.getDataName()
        );
    }

    private static @NotNull String toStringPath(@NotNull String ...path){
        return String.join(".", path);
    }

    public static void loadConfig() {
        config = CoreU.getInstance().getConfig();
    }
    public static void saveConfig() {
        CoreU.getInstance().saveConfig();
    }
}
