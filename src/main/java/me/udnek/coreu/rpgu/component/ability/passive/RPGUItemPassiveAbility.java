package me.udnek.coreu.rpgu.component.ability.passive;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUPassiveItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.util.Either;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RPGUItemPassiveAbility extends RPGUItemAbility<Integer>, CustomComponent<RPGUPassiveItem> {
    @NotNull CustomEquipmentSlot getSlot();

    default void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot, int tickDelay){
        activate(customItem, player, new Either<>(slot, null), tickDelay);
    }

    @Override
    @NotNull
    default String translationKey(){return "passive_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}
}
