package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@org.jspecify.annotations.NullMarked public class EntityPlacingItem implements CustomComponent<CustomItem>{

    public static final EntityPlacingItem EMPTY = new EntityPlacingItem(){
        @Override
        public void onPlace(PlayerInteractEvent event) {}
    };

    protected CustomEntityType entity;

    private EntityPlacingItem(){
        entity = null;
    }

    public EntityPlacingItem(CustomEntityType entityType){
        this.entity = entityType;
    }

    public void onPlace(PlayerInteractEvent event){
        if (!event.hasBlock()) return;
        if (!event.getPlayer().getGameMode().isInvulnerable()) {
            ItemStack item = event.getItem();
            assert item != null;
            item.setAmount(item.getAmount() - 1);
        }

        assert event.getHand() != null;
        event.getPlayer().swingHand(event.getHand());

        Block clickedBlock = event.getClickedBlock();
        assert clickedBlock != null;
        Block relative = clickedBlock.getRelative(event.getBlockFace());

        entity.spawn(relative.getLocation());
    }

    @Override
    public CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.ENTITY_PLACING_ITEM;
    }
}
