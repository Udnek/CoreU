package me.udnek.coreu.resourcepack.misc;

import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked
public enum RpFileType {

    PNG("png"),
    JSON("json"),
    MCMETA("mcmeta"),
    VSH("vsh"),
    FSH("fsh"),
    OGG("ogg"),
    TTF("ttf"),
    UNKNOWN(null);

    public final @Nullable String extension;
    RpFileType(@Nullable String extension){
        this.extension = extension;
    }
    public static RpFileType get(String path){
        for (RpFileType value : values()) {
            if (value.extension == null) continue;
            if (path.endsWith("."+value.extension)) return value;
        }
        return UNKNOWN;
    }
}
