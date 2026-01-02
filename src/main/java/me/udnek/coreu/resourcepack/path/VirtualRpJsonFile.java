package me.udnek.coreu.resourcepack.path;

import com.google.gson.JsonElement;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class VirtualRpJsonFile extends RpPath{

    protected JsonElement data;

    public VirtualRpJsonFile(@Nullable VirtualResourcePack resourcePack, @NotNull JsonElement data, @NotNull String path) {
        super(resourcePack, path);
        this.data = data;
    }
    public VirtualRpJsonFile(@NotNull JsonElement data, @NotNull String path){
        this(null, data, path);
    }

    public @NotNull JsonElement getData() {
        return data;
    }

    @Override
    public @NotNull InputStream getInputStream() {
        return IOUtils.toInputStream(data.toString(), StandardCharsets.UTF_8);
    }
}
