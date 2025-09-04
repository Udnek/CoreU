package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public interface RightClickableBlock extends CustomComponent<CustomBlockType> {

    RightClickableBlock EMPTY = (block, event) -> {};

    void onRightClick(@NotNull CustomBlockType block, @NotNull PlayerInteractEvent event);

    @Override
    @NotNull
    default CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.RIGHT_CLICKABLE_BLOCK;
    }
}
