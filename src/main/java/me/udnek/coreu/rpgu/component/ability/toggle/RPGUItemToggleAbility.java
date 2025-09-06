package me.udnek.coreu.rpgu.component.ability.toggle;

import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.ability.passive.RPGUItemPassiveAbility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RPGUItemToggleAbility<ToggleContext> extends RPGUItemPassiveAbility<ToggleContext>{
    boolean isToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot);
}
