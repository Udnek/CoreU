package me.udnek.coreu.custom.component.instance;

import io.papermc.paper.event.player.PlayerPickBlockEvent;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MiddleClickableBlock extends CustomComponent<CustomBlockType> {

    static MiddleClickableBlock DEFAULT = new MiddleClickableBlock() {
        @Override
        public void onMiddleClick(@NotNull CustomBlockType block, @NotNull PlayerPickBlockEvent event) {
        }
    };

//    static void modifyEvent(@NotNull PlayerPickBlockEvent event, @NotNull ItemStack newItem){
//        event.setCancelled(true);
//        event.
//    }

    void onMiddleClick(@NotNull CustomBlockType block, @NotNull PlayerPickBlockEvent event);

    @Override
    default @NotNull CustomComponentType<? extends CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType() {
        return CustomComponentType.MIDDLE_CLICKABLE_BLOCK;
    }
}
