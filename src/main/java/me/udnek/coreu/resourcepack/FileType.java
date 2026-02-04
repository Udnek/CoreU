package me.udnek.coreu.resourcepack;

import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public enum FileType{

    PNG("png"),
    JSON("json"),
    MCMETA("mcmeta"),
    VSH("vsh"),
    FSH("fsh"),
    OGG("ogg"),
    TTF("ttf"),
    UNKNOWN(null);

    public final @Nullable String extension;
    FileType(@Nullable String extension){
        this.extension = extension;
    }
    public static FileType get(String path){
        for (FileType value : values()) {
            if (value.extension == null) continue;
            if (path.endsWith("."+value.extension)) return value;
        }
        return UNKNOWN;
    }
}
