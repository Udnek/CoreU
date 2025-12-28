package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.jetbrains.annotations.NotNull;

public interface PassiveAbilityLorePart extends AbilityLorePart{

    void setEquipmentSlot(@NotNull CustomEquipmentSlot slot);
}
