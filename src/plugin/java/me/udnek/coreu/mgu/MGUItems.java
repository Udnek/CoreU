package me.udnek.coreu.mgu;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;

@org.jspecify.annotations.NullMarked public class MGUItems{

    public static final CustomItem COORDINATE_WAND = register(new CoordinateWand());

    public static CustomItem register(CustomItem customItem){
        return CustomRegistries.ITEM.register(CoreU.getInstance(), customItem);
    }
}
