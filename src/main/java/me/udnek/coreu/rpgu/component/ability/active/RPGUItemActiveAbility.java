package me.udnek.coreu.rpgu.component.ability.active;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.equipmentslot.slot.SingleSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.component.RPGUActiveAbilityItem;
import me.udnek.coreu.util.Either;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RPGUItemActiveAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUActiveAbilityItem> {


}
