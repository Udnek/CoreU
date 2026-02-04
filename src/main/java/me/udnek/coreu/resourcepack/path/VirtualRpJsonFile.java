package me.udnek.coreu.resourcepack.path;

import com.google.gson.JsonElement;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@org.jspecify.annotations.NullMarked public class VirtualRpJsonFile extends RpPath{

    protected JsonElement data;

    public VirtualRpJsonFile(@Nullable VirtualResourcePack resourcePack, JsonElement data, String path) {
        super(resourcePack, path);
        this.data = data;
    }
    public VirtualRpJsonFile(JsonElement data, String path){
        this(null, data, path);
    }

    public JsonElement getData() {
        return data;
    }

    @Override
    public InputStream getInputStream() {
        return IOUtils.toInputStream(data.toString(), StandardCharsets.UTF_8);
    }
}
