package me.udnek.coreu.rpgu.component.ability.passive;

import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbstractAbility;
import me.udnek.coreu.rpgu.lore.AttributesLorePart;
import me.udnek.coreu.rpgu.lore.ability.PassiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class RPGUConstructablePassiveAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext> implements RPGUItemPassiveAbility<ActivationContext> {

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
        lorePart.setEquipmentSlot(getSlot());
        lorePart.setHeader(Component.translatable("rpgu_passive_ability.coreu.title"));
        addDescriptionLines(lorePart);
        addPropertyLines(lorePart);
    }

    @Override
    public void activate(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity, boolean cancelActivationContextIfCooldown, @NotNull UniversalInventorySlot slot, @NotNull ActivationContext activationContext) {
        if (!getSlot().intersects(livingEntity, slot)) return;
        super.activate(customItem, livingEntity, cancelActivationContextIfCooldown, slot, activationContext);
    }
}
