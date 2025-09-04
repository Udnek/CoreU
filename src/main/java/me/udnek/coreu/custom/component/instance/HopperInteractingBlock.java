package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.jetbrains.annotations.NotNull;

public interface HopperInteractingBlock extends CustomComponent<CustomBlockType> {

    HopperInteractingBlock DENY = new HopperInteractingBlock() {
        @Override
        public void onHopperInteract(@NotNull CustomBlockType blockType, @NotNull HopperInventorySearchEvent event) {
            event.setInventory(null);
        }
        @Override
        public void onItemMoveInto(@NotNull CustomBlockType blockType, @NotNull InventoryMoveItemEvent event) {
            event.setCancelled(true);
        }
        @Override
        public void onItemMoveFrom(@NotNull CustomBlockType blockType, @NotNull InventoryMoveItemEvent event) {
            event.setCancelled(true);
        }
    };

    void onHopperInteract(@NotNull CustomBlockType blockType, @NotNull HopperInventorySearchEvent event);

    void onItemMoveInto(@NotNull CustomBlockType blockType, @NotNull InventoryMoveItemEvent event);
    void onItemMoveFrom(@NotNull CustomBlockType blockType, @NotNull InventoryMoveItemEvent event);


    @Override
    @NotNull
    default CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.HOPPER_INTERACTING_BLOCK;
    }
}
