package me.udnek.coreu.custom.hud;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public  interface CustomHud{
    Component getText(Player player);
}
