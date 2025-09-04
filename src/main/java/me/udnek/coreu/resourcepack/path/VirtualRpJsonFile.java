package me.udnek.coreu.resourcepack.path;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

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
        return IOUtils.toInputStream(data.toString(), Charsets.UTF_8);
    }
}
