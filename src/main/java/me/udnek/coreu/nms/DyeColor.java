package me.udnek.coreu.nms;

import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DyeColor{
    String name();
    Color textureDiffuseColor();
    Color fireworkColor();
    Color textColor();
}
