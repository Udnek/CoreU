package me.udnek.coreu.util;

import me.udnek.coreu.CoreU;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@org.jspecify.annotations.NullMarked
public class LogUtils {

    public static void log(Plugin plugin, @Nullable Component message) {
        if (message == null) message = Component.text("null");
        Bukkit.getConsoleSender().sendMessage(Component.text("["+plugin.getName()+"] ").append(message));
    }
    public static void log(Plugin plugin, @Nullable Object message) {
        log(plugin, Component.text(String.valueOf(message)));
    }
    public static void log(Plugin plugin, @Nullable Object message, TextColor color) {
        log(plugin, Component.text(String.valueOf(message)).color(color));
    }
    public static void log(Plugin plugin, List<Component> components) {
        components.forEach(c -> log(plugin, c));
    }

    public static void coreuLog(@Nullable Object message){
        log(CoreU.getInstance(), message, NamedTextColor.WHITE);
    }
    public static void coreuWarning(@Nullable Object message){
        log(CoreU.getInstance(), message, NamedTextColor.YELLOW);
    }
    public static void coreuError(@Nullable Object message){
        log(CoreU.getInstance(), message, NamedTextColor.RED);
    }

    public static void logDeclaredFields(Object object){
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            String message = field.toString();
            if (field.accessFlags().contains(AccessFlag.PRIVATE)){
                log(CoreU.getInstance(), message, TextColor.color(1f, 0,0f));
            }
            else if(field.accessFlags().contains(AccessFlag.FINAL)){
                log(CoreU.getInstance(), message, TextColor.color(1f, 1f, 0));
            }
            else {
                log(CoreU.getInstance(), message);
            }
        }
    }

    public static void logDeclaredMethods(Object object){
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            String message = method.toString();
            if (method.accessFlags().contains(AccessFlag.PRIVATE)){
                log(CoreU.getInstance(), message, TextColor.color(1f, 0,0f));
            }
            else if(method.accessFlags().contains(AccessFlag.FINAL)){
                log(CoreU.getInstance(), message, TextColor.color(1f, 1f, 0));
            }
            else {
                log(CoreU.getInstance(), message);
            }
        }

    }
}
