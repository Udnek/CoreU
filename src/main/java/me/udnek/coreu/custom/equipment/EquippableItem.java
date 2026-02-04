package me.udnek.coreu.custom.equipment;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public  interface EquippableItem extends CustomComponent<CustomItem>{
    boolean isAppropriate(CustomItem item, Player player, BaseUniversalSlot slot);

    void onEquipped(CustomItem item, Player player, BaseUniversalSlot slot);
    void onUnequipped(CustomItem item, Player player, BaseUniversalSlot slot);
    void tick(CustomItem customItem, Player player, BaseUniversalSlot slot, int tickDelay);
}
