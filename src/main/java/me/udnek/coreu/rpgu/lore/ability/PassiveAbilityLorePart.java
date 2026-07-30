package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface PassiveAbilityLorePart extends AbilityLorePart{

    void setEquipmentSlot(CustomEquipmentSlot slot);
}
