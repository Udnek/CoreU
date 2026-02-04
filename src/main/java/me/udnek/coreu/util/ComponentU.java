package me.udnek.coreu.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class ComponentU{

    @Deprecated
    public static final TextColor NO_SHADOW_COLOR = TextColor.fromHexString("#4e5c24");
    public static final Key SPACE_FONT = new NamespacedKey("space", "default");

    public static Component translatableWithInsertion(String key, Component ...insertion){
        return Component.translatable(key, List.of(insertion));
    }

    public static List<Component> translate(String ...keys){
        List<Component> components = new ArrayList<>();
        for (String key : keys) {
            components.add(Component.translatable(key));
        }
        return components;
    }

    public static Component space(int n){
        return Component.translatable("space."+n);
    }

    public static Component spaceSpaceFont(int n){
        return Component.translatable("space."+n).font(SPACE_FONT);
    }

    public static Component textWithNoSpace(int offset, Component text, int size){
        if (size != 0) size+=1; // SHADOW COUNTS
        return ComponentU.space(offset).append(text).append(ComponentU.space(-size-offset));
    }
    public static Component textWithNoSpace(Component text, int size){
        if (size == 0) return text;
        size += 1; // SHADOW COUNTS
        return text.append(ComponentU.space(-size));
    }

    public static Component textWithNoSpaceSpaceFont(int offset, Component text, int size){
        if (size != 0) size+=1; // SHADOW COUNTS
        return ComponentU.spaceSpaceFont(offset).append(text).append(ComponentU.spaceSpaceFont(-size-offset));
    }
    public static Component textWithNoSpaceSpaceFont(Component text, int size){
        if (size == 0) return text;
        size += 1; // SHADOW COUNTS
        return text.append(ComponentU.spaceSpaceFont(-size));
    }
}
