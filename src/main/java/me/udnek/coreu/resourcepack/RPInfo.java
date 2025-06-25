package me.udnek.coreu.resourcepack;

import me.udnek.coreu.serializabledata.SerializableData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RPInfo implements SerializableData {

    public @Nullable String extractDirectory;
    public @Nullable String checksum_zip;
    public @Nullable String checksum_folder;
    public @NotNull String ip = "127.0.0.1";
    public int port = 25566;

    public RPInfo(){}

    @Override
    public @NotNull String serialize() {
        return SerializableData.serializeMap(Map.of(
                "extract_directory", extractDirectory == null ? "null": extractDirectory ,
                "checksum_zip", checksum_zip == null ? "null": checksum_zip,
                "checksum_folder", checksum_folder == null ? "null": checksum_folder,
                "ip", ip,
                "port", port));
    }
    @Override
    public void deserialize(@Nullable String data) {
        Map<String, String> map = SerializableData.deserializeMap(data);
        extractDirectory = map.getOrDefault("extract_directory", "");
        checksum_zip = map.getOrDefault("checksum_zip", "");
        checksum_folder = map.getOrDefault("checksum_folder", "");
        ip = map.getOrDefault("ip", "127.0.0.1");
        port = Integer.parseInt(map.getOrDefault("port", "25565"));
    }
    @Override
    public @NotNull String getDataName() {
        return "resourcepack_settings";
    }
}
