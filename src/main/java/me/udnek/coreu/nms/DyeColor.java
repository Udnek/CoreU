package me.udnek.coreu.nms;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public interface DyeColor {
    @NotNull String name();
    @NotNull Color textureDiffuseColor();
    @NotNull Color fireworkColor();
    @NotNull Color textColor();
}
