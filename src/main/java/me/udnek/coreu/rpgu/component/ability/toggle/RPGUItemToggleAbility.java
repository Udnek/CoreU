package me.udnek.coreu.rpgu.component.ability.toggle;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUToggleItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.util.Either;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RPGUItemToggleAbility extends RPGUItemAbility<Integer>, CustomComponent<RPGUToggleItem> {

    @NotNull CustomEquipmentSlot getSlot();

    default void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot, int tickDelay){
        activate(customItem, player, new Either<>(slot, null), tickDelay);
    }

    boolean isToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot);
    @Override
    @NotNull
    default String translationKey(){return "toggle_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}
}
