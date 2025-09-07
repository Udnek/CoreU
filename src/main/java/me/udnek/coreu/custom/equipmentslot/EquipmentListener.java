package me.udnek.coreu.custom.equipmentslot;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.util.SelfRegisteringListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EquipmentListener extends SelfRegisteringListener {
    public EquipmentListener(JavaPlugin plugin) {
        super(plugin);
    }

    private void proceed(@NotNull Player player, int oldSlot, int newSlot, @Nullable ItemStack oldStack, @Nullable ItemStack newStack){
        PlayerEquipment data = PlayerEquipmentManager.getInstance().getData(player);
        BaseUniversalSlot oldUniversal = new BaseUniversalSlot(oldSlot);
        BaseUniversalSlot newUniversal = new BaseUniversalSlot(newSlot);

        CustomItem customItem;

        customItem = CustomItem.get(oldStack);
        if (customItem != null){
            Map.Entry<BaseUniversalSlot, CustomItem> foundEntry = data.getFirst(oldUniversal);
            if (foundEntry != null){

                if (foundEntry.getValue() != customItem) System.out.println("Data item: " + foundEntry.getValue().getId() +"; Real item: " + customItem.getId());
                data.set(foundEntry.getKey(), null);
                for (EquippableItem equippableItem : customItem.getComponents().getAllTyped(EquippableItem.class)) {
                    if (equippableItem.isAppropriate(customItem, player, oldUniversal)) equippableItem.onUnequipped(foundEntry.getValue(), player, oldUniversal);
                }

            }
        }

        customItem = CustomItem.get(newStack);
        if (customItem != null){
            boolean atLeastOne = false;
            for (EquippableItem equippableItem : customItem.getComponents().getAllTyped(EquippableItem.class)) {
                if (equippableItem.isAppropriate(customItem, player, newUniversal)){
                    atLeastOne = true;
                    equippableItem.onEquipped(customItem, player, newUniversal);
                }
            }
            if (atLeastOne) data.set(newUniversal, customItem);
        }
    }

    @EventHandler
    public void onHotbarScroll(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getNewSlot() != inventory.getHeldItemSlot()) return;
                proceed(player, event.getPreviousSlot(), event.getNewSlot(), inventory.getItem(event.getPreviousSlot()), inventory.getItem(event.getNewSlot()));
            }
        }.runTask(CoreU.getInstance());
        proceed(player, event.getPreviousSlot(), event.getNewSlot(), inventory.getItem(event.getPreviousSlot()), inventory.getItem(event.getNewSlot()));
    }

    @EventHandler
    public void onPlayerEquipsItem(PlayerInventorySlotChangeEvent event){
        proceed(event.getPlayer(), event.getSlot(), event.getSlot(), event.getOldItemStack(), event.getNewItemStack());
    }
}











