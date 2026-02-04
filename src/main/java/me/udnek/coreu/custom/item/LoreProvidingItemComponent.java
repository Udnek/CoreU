package me.udnek.coreu.custom.item;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.util.LoreBuilder;

@org.jspecify.annotations.NullMarked public  interface LoreProvidingItemComponent extends CustomComponent<CustomItem>{
    void getLore(CustomItem customItem, LoreBuilder loreBuilder);
}
