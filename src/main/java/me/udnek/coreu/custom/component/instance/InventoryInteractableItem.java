package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.event.inventory.InventoryClickEvent;

@org.jspecify.annotations.NullMarked public  interface InventoryInteractableItem extends CustomComponent<CustomItem>{
    InventoryInteractableItem EMPTY = new InventoryInteractableItem() {
        @Override
        public void onBeingClicked(CustomItem item, InventoryClickEvent event) {}
        @Override
        public void onClickWith(CustomItem item, InventoryClickEvent event) {}
    };

    void onBeingClicked(CustomItem item, InventoryClickEvent event);
    void onClickWith(CustomItem item, InventoryClickEvent event);

    @Override
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType(){
        return CustomComponentType.INVENTORY_INTERACTABLE_ITEM;
    }
}
