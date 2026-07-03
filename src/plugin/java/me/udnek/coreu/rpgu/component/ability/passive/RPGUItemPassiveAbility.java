package me.udnek.coreu.rpgu.component.ability.passive;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUPassiveItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public  interface RPGUItemPassiveAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUPassiveItem>{
    CustomEquipmentSlot getSlot();

    void tick(CustomItem customItem, Player player, BaseUniversalSlot slot, int tickDelay);

    @Override
    default String translationKey(){return "passive_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}
}
