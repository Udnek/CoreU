package me.udnek.coreu.rpgu.component.ability.active;

import me.udnek.coreu.rpgu.component.ability.RPGUItemAbstractAbility;
import me.udnek.coreu.rpgu.lore.ability.ActiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class RPGUConstructableActiveAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext> implements RPGUItemActiveAbility<ActivationContext> {

    public void addPropertyLines(@NotNull ActiveAbilityLorePart componentable){
        getProperties().forEach(c -> c.describe(componentable));
    }


    @Override
    public void getLore(@NotNull LoreBuilder loreBuilder){
        ActiveAbilityLorePart lorePart = new ActiveAbilityLorePart();
        loreBuilder.set(55, lorePart);
        lorePart.setHeader(Component.translatable("rpgu_active_ability.coreu.title"));
        addPropertyLines(lorePart);
    }
}
