package me.udnek.coreu.custom.component.instance;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class MiddleClickable {

    protected void onMiddleClick(ItemStack itemToFindOrGiveIfNotInInventory, PlayerPickItemEvent event) {
        event.setCancelled(true);
        final @Nullable BaseUniversalSlot[] sourceSlotL = {null};
        Player player = event.getPlayer();
        UniversalInventorySlot.iterateThroughNotEmpty(
                (baseUniversalSlot, stack) -> {
                    if (sourceSlotL[0] != null) return;
                    if (CustomItem.get(stack) == CustomItem.get(itemToFindOrGiveIfNotInInventory)) sourceSlotL[0] = baseUniversalSlot;
                }, player
        );
        BaseUniversalSlot sourceSlot = sourceSlotL[0];
        if (sourceSlot == null){
            if (player.getGameMode() != GameMode.CREATIVE) return;
            player.getInventory().setItem(event.getTargetSlot(), itemToFindOrGiveIfNotInInventory);
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
}
