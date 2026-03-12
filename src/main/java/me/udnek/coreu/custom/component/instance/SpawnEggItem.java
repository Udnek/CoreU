package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@org.jspecify.annotations.NullMarked
public class SpawnEggItem implements CustomComponent<CustomItem>{

    public static final SpawnEggItem EMPTY = new SpawnEggItem(){
        @Override
        public @Nullable Entity onPlace(PlayerInteractEvent event) {return null;}
    };

    protected final @Nullable CustomEntityType entity;

    protected SpawnEggItem(){
        entity = null;
    }

    public SpawnEggItem(CustomEntityType entityType) {
        this.entity = entityType;
    }

    protected @Nullable CustomEntityType getEntityType(PlayerInteractEvent event){
        return entity;
    }

    public @Nullable Entity onPlace(PlayerInteractEvent event){
        CustomEntityType entityType = getEntityType(event);
        if (entityType == null) return null;
        if (!event.hasBlock()) return null;
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

        return entityType.spawn(relative.getLocation());
    }

    @Override
    public CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.SPAWN_EGG_ITEM;
    }
}
