package me.udnek.coreu.custom.item;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

public interface LoreProvidingItemComponent extends CustomComponent<CustomItem> {
    default void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder){}
}
