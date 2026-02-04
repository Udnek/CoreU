package me.udnek.coreu.nms;

import net.minecraft.world.level.biome.Biome;

@org.jspecify.annotations.NullMarked public enum DownfallType{
    NONE,
    RAIN,
    SNOW;

    public static DownfallType fromNMS(Biome.Precipitation precipitation){
        return switch (precipitation){
            case RAIN -> RAIN;
            case SNOW -> SNOW;
            default -> NONE;
        };
    }
}
