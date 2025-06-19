package me.udnek.coreu.rpgu.component.ability.passive;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.component.RPGUPassiveAbilityItem;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface RPGUItemPassiveAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUPassiveAbilityItem> {
    @NotNull Set<CustomEquipmentSlot> getSlot();
}
