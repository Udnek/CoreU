package me.udnek.coreu.rpgu.component.ability.toggle;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUToggleItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RPGUItemToggleAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUToggleItem> {

    @NotNull CustomEquipmentSlot getSlot();

    void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull UniversalInventorySlot slot, int tickDelay);

    boolean isToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull UniversalInventorySlot slot);
    @Override
    @NotNull
    default String translationKey(){return "toggle_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}
}
