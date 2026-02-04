package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import org.bukkit.event.player.PlayerInteractEvent;

@org.jspecify.annotations.NullMarked public  interface RightClickableBlock extends CustomComponent<CustomBlockType>{

    RightClickableBlock EMPTY = (block, event) -> {};

    void onRightClick(CustomBlockType block, PlayerInteractEvent event);

    @Override
    default CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.RIGHT_CLICKABLE_BLOCK;
    }
}
