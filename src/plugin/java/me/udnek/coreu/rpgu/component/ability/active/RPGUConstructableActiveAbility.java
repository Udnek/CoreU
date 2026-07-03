package me.udnek.coreu.rpgu.component.ability.active;

import me.udnek.coreu.rpgu.component.ability.RPGUItemAbstractAbility;
import me.udnek.coreu.rpgu.lore.ability.ActiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;

@org.jspecify.annotations.NullMarked public abstract class RPGUConstructableActiveAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext>implements RPGUItemActiveAbility<ActivationContext>{


    @Override
    public void getLore(LoreBuilder loreBuilder){
        ActiveAbilityLorePart lorePart = new ActiveAbilityLorePart();
        loreBuilder.set(55, lorePart);
        lorePart.setHeader(Component.translatable("rpgu_active_ability.coreu.title"));
        addDescriptionLines(lorePart);
        addPropertyLines(lorePart);
    }
}
