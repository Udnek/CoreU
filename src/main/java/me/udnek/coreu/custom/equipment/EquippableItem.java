package me.udnek.coreu.custom.equipment;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EquippableItem extends CustomComponent<CustomItem> {
    boolean isAppropriate(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot);

    void onEquipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot);
    void onUnequipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot);
    void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot, int tickDelay);
}
