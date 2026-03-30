package me.udnek.coreu.custom.item.instace;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.jetbrains.annotations.NotNull;

public class Items {
    public static final CustomItem ABSTRACT_SPAWN_EGG =  register(new UniversalSpawnEgg());


    private static @NotNull CustomItem register(@NotNull CustomItem customItem){
        return CustomRegistries.ITEM.register(CoreU.getInstance(), customItem);
    }
}
