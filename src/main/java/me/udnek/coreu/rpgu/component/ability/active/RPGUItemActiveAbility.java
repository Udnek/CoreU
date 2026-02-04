package me.udnek.coreu.rpgu.component.ability.active;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.rpgu.component.RPGUActiveItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;

@org.jspecify.annotations.NullMarked public  interface RPGUItemActiveAbility<ActivationContext> extends RPGUItemAbility<ActivationContext>, CustomComponent<RPGUActiveItem>{

    @Override
    default String translationKey(){{return "active_ability." + getType().getKey().namespace() + "." + getType().getKey().value();}}

}
