package me.udnek.coreu.custom.hud;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface CustomHud{
    Component getText(Player player);
}
