package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.event.block.BlockPlaceEvent;

@org.jspecify.annotations.NullMarked public class BlockPlacingItem implements CustomComponent<CustomItem>{

    public static final BlockPlacingItem EMPTY = new BlockPlacingItem(){
        @Override
        public void onPlace(BlockPlaceEvent event) {}
    };

    protected CustomBlockType block;

    private BlockPlacingItem(){
        block = null;
    }

    public BlockPlacingItem(CustomBlockType blockType){
        this.block = blockType;
    }

    public void onPlace(BlockPlaceEvent event){
        block.place(event.getBlock().getLocation(), new CustomBlockPlaceContext(event.getPlayer(), event.getBlockAgainst(), event));
    }

    @Override
    public CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.BLOCK_PLACING_ITEM;
    }
}
