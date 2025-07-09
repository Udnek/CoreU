package me.udnek.coreu.util;

import me.udnek.coreu.CoreU;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class LogUtils {
    public static void log(@Nullable Object message) {log(Component.text(String.valueOf(message)));}
    public static void log(@Nullable Object message, @Nullable TextColor color){ log(Component.text(String.valueOf(message)).color(color));}
    public static void log(@Nullable Component message){ Bukkit.getConsoleSender().sendMessage(message);}
    public static void log(@Nullable List<Component> components){components.forEach(LogUtils::log);}

    public static void pluginLog(@Nullable Object message){
        CoreU.getInstance().getLogger().info(String.valueOf(message));
    }
    public static void pluginWarning(@Nullable Object message){
        CoreU.getInstance().getLogger().warning(String.valueOf(message));
    }

    public static void logDeclaredFields(@NotNull Object object){
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            String message = field.toString();
            if (field.accessFlags().contains(AccessFlag.PRIVATE)){
                log(message, TextColor.color(1f, 0,0f));
            }
            else if(field.accessFlags().contains(AccessFlag.FINAL)){
                log(message, TextColor.color(1f, 1f, 0));
            }
            else {
                log(message);
            }
        }
    }

    public static void logDeclaredMethods(@NotNull Object object){
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            String message = method.toString();
            if (method.accessFlags().contains(AccessFlag.PRIVATE)){
                log(message, TextColor.color(1f, 0,0f));
            }
            else if(method.accessFlags().contains(AccessFlag.FINAL)){
                log(message, TextColor.color(1f, 1f, 0));
            }
            else {
                log(message);
            }
        }

    }
}
