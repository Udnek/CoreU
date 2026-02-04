package me.udnek.coreu.serializabledata;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@org.jspecify.annotations.NullMarked public  interface SerializableData{

    static String serializeMap(Map<String, Object> data){
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return builder.toString();
    }
    static Map<String, String> deserializeMap(@Nullable String string){
        if (string == null) return Map.of();
        Map<String, String> data = new HashMap<>();
        String[] split = string.split(";");
        for (String sub : split) {
            String[] keyAndValue = sub.split("=");
            data.put(keyAndValue[0], keyAndValue[1]);
        }
        return data;
    }

    String serialize();
    void deserialize(@Nullable String data);

    default void write(Plugin plugin){
        SerializableDataManager.write(this, plugin);
    }

    default void read(Plugin plugin){
        SerializableDataManager.read(this, plugin);
    }

    String getDataName();
}
