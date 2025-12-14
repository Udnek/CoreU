package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public interface LeftClickableItem extends CustomComponent<CustomItem> {

    LeftClickableItem EMPTY = (item, event) -> {};

    void onLeftClick(@NotNull CustomItem item, @NotNull PlayerInteractEvent event);

    @Override
    @NotNull
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType(){
        return CustomComponentType.LEFT_CLICKABLE_ITEM;
    }
}
