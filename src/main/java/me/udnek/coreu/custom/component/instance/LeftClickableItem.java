package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.event.player.PlayerInteractEvent;

@org.jspecify.annotations.NullMarked public  interface LeftClickableItem extends CustomComponent<CustomItem>{

    LeftClickableItem EMPTY = (item, event) -> {};

    void onLeftClick(CustomItem item, PlayerInteractEvent event);

    @Override
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType(){
        return CustomComponentType.LEFT_CLICKABLE_ITEM;
    }
}
