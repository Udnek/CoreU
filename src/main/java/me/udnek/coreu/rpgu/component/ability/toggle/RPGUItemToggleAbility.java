package me.udnek.coreu.rpgu.component.ability.toggle;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUToggleItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public  interface RPGUItemToggleAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUToggleItem>{

    CustomEquipmentSlot getSlot();

    void tick(CustomItem customItem, Player player, UniversalInventorySlot slot, int tickDelay);

    boolean isToggled(CustomItem customItem, Player player, UniversalInventorySlot slot);
    @Override
    default String translationKey(){return "toggle_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}
}
