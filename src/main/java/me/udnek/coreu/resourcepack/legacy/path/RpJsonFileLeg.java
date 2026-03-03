package me.udnek.coreu.resourcepack.legacy.path;

import com.google.gson.JsonElement;
import me.udnek.coreu.resourcepack.legacy.VirtualResourcePackLeg;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Deprecated
@org.jspecify.annotations.NullMarked
public class RpJsonFileLeg extends RpPath{

    protected JsonElement data;

    public RpJsonFileLeg(@Nullable VirtualResourcePackLeg resourcePack, JsonElement data, String path) {
        super(resourcePack, path);
        this.data = data;
    }
    public RpJsonFileLeg(JsonElement data, String path){
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
