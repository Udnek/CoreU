package me.udnek.coreu.serializabledata;

import me.udnek.coreu.CoreU;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@org.jspecify.annotations.NullMarked public class SerializableDataManager{

    private static @UnknownNullability FileConfiguration config;
    private static final String PLUGIN_PATH = "plugin";
    private static final String PLAYER_PATH = "player";
    ///////////////////////////////////////////////////////////////////////////
    public static <T extends SerializableData> T read(@NotNull T data, Plugin plugin, Player player){
        String readRaw = readRaw(getPath(data, plugin, player));
        data.deserialize(readRaw);
        return data;
    }
    public static <T extends SerializableData> T read(@NotNull T data, Plugin plugin){
        String readRaw = readRaw(getPath(data, plugin));
        data.deserialize(readRaw);
        return data;
    }
    private static @Nullable String readRaw(String path){
        loadConfig();
        return config.getString(path);
    }
    ///////////////////////////////////////////////////////////////////////////
    public static void write(SerializableData data, Plugin plugin, Player player){
        writeRaw(data, getPath(data, plugin, player));
    }
    public static void write(SerializableData data, Plugin plugin){
        writeRaw(data, getPath(data, plugin));
    }
    private static void writeRaw(SerializableData serializableData, String path){
        loadConfig();
        config.set(path, serializableData.serialize());
        saveConfig();
    }
    ///////////////////////////////////////////////////////////////////////////
    private static String getPath(SerializableData data, Plugin plugin, Player player){
        return toStringPath(
                PLAYER_PATH,
                player.getUniqueId().toString(),
                plugin.getName() + ":" + data.getDataName()
        );
    }
    private static String getPath(SerializableData data, Plugin plugin){
        return toStringPath(
                PLUGIN_PATH,
                plugin.getName(),
                data.getDataName()
        );
    }

    private static String toStringPath(String ...path){
        return String.join(".", path);
    }

    public static void loadConfig() {
        config = CoreU.getInstance().getConfig();
    }
    public static void saveConfig() {
        CoreU.getInstance().saveConfig();
    }
}
