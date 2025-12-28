package me.udnek.coreu.custom.component.instance;

import io.papermc.paper.event.player.PlayerPickBlockEvent;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface MiddleClickableBlock extends CustomComponent<CustomBlockType> {

    MiddleClickableBlock DEFAULT = new Implementation();

    void onMiddleClick(@NotNull CustomBlockType block, @NotNull PlayerPickBlockEvent event);

    @Override
    @NotNull
    default CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.MIDDLE_CLICKABLE_BLOCK;
    }

    class Implementation implements MiddleClickableBlock{

        @Override
        public void onMiddleClick(@NotNull CustomBlockType block, @NotNull PlayerPickBlockEvent event) {
            event.setCancelled(true);
            CustomItem item = block.getItem();
            if (item == null) return;
            final BaseUniversalSlot[] sourceSlotL = {null};
            Player player = event.getPlayer();
            UniversalInventorySlot.iterateThroughNotEmpty(
                    (baseUniversalSlot, stack) -> {
                        if (sourceSlotL[0] != null) return;
                        if (CustomItem.get(stack) == item) sourceSlotL[0] = baseUniversalSlot;
                    }, player
            );
            BaseUniversalSlot sourceSlot = sourceSlotL[0];
            if (sourceSlot == null){
                if (player.getGameMode() != GameMode.CREATIVE) return;
                player.getInventory().setItem(event.getTargetSlot(), getItemForCreative(block, event));
                player.getInventory().setHeldItemSlot(event.getTargetSlot());
            } else {
                if (sourceSlot.slot == null) return;
                if (0 <= sourceSlot.slot && sourceSlot.slot <= 8){
                    player.getInventory().setHeldItemSlot(sourceSlot.slot);
                } else {
                    // SWAPPING
                    ItemStack sourceItem = sourceSlot.getItem(player);
                    sourceSlot.setItem(player.getInventory().getItem(event.getTargetSlot()), player); // target -> source
                    player.getInventory().setItem(event.getTargetSlot(), sourceItem); // source -> target
                    player.getInventory().setHeldItemSlot(event.getTargetSlot());
                }
            }
        }

        public @NotNull ItemStack getItemForCreative(@NotNull CustomBlockType block, @NotNull PlayerPickBlockEvent event){
            return Objects.requireNonNull(block.getItem()).getItem();
        }
    }
}
