package me.udnek.coreu.custom.item;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.util.LoreBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface LoreProvidingItemComponent extends CustomComponent<CustomItem>{
    void getLore(CustomItem customItem, LoreBuilder loreBuilder);
}
