package me.udnek.coreu.rpgu.component.ability.passive;

import me.udnek.coreu.rpgu.component.ability.RPGUItemAbstractAbility;
import me.udnek.coreu.rpgu.lore.AttributesLorePart;
import me.udnek.coreu.rpgu.lore.ability.PassiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class RPGUConstructablePassiveAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext> implements RPGUItemPassiveAbility<ActivationContext> {


    public void addPropertyLines(@NotNull PassiveAbilityLorePart componentable){
        getProperties().forEach(c -> c.describe(componentable));
    }

    @Override
    public void getLore(@NotNull LoreBuilder loreBuilder){
        LoreBuilder.Componentable componentable = loreBuilder.get(LoreBuilder.Position.ATTRIBUTES);
        PassiveAbilityLorePart lorePart;
        if (!(componentable instanceof AttributesLorePart attributesLorePart)){
            AttributesLorePart newPart = new AttributesLorePart();
            loreBuilder.set(LoreBuilder.Position.ATTRIBUTES, newPart);
            lorePart = newPart;
        } else {
            lorePart = attributesLorePart;
        }

        // TODO WORK AROUND SET<SLOT>
        lorePart.setEquipmentSlot(getSlot().stream().findFirst().orElseThrow());
        lorePart.setHeader(Component.translatable("rpgu_passive_ability.coreu.title"));
        addPropertyLines(lorePart);
    }
}
