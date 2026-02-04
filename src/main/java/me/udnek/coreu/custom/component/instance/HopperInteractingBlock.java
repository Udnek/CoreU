package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

@org.jspecify.annotations.NullMarked public  interface HopperInteractingBlock extends CustomComponent<CustomBlockType>{

    HopperInteractingBlock DENY = new HopperInteractingBlock() {
        @Override
        public void onHopperSearch(CustomBlockType blockType, HopperInventorySearchEvent event) {
            event.setInventory(null);
        }
        @Override
        public void onItemMoveInto(CustomBlockType blockType, InventoryMoveItemEvent event) {
            event.setCancelled(true);
        }
        @Override
        public void onItemMoveFrom(CustomBlockType blockType, InventoryMoveItemEvent event) {
            event.setCancelled(true);
        }
    };

    void onHopperSearch(CustomBlockType blockType, HopperInventorySearchEvent event);

    void onItemMoveInto(CustomBlockType blockType, InventoryMoveItemEvent event);
    void onItemMoveFrom(CustomBlockType blockType, InventoryMoveItemEvent event);


    @Override
    default CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.HOPPER_INTERACTING_BLOCK;
    }
}
