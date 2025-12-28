package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.rpgu.component.ability.property.function.AttributeFunction;
import me.udnek.coreu.rpgu.component.ability.property.function.RPGUPropertyFunction;
import me.udnek.coreu.rpgu.component.ability.property.type.AttributeBasedPropertyType;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class AttributeBasedProperty implements RPGUAbilityProperty<LivingEntity, Double> {

    protected @NotNull AttributeBasedPropertyType type;
    protected @NotNull RPGUPropertyFunction<LivingEntity, Double> function;

    public AttributeBasedProperty(@NotNull AttributeFunction function, @NotNull AttributeBasedPropertyType type) {
        this.function = function;
        this.type = type;
    }

    public AttributeBasedProperty(double base, @NotNull AttributeBasedPropertyType type) {
        this(new AttributeFunction(type.getAttribute(), base), type);
    }

    @Override
    public @NotNull AttributeBasedPropertyType getType() {return type;}

    public @NotNull RPGUPropertyFunction<LivingEntity, Double> getFunction() {
        return function;
    }

    @Override
    public @NotNull Double getBase() {
        return function.getBase();
    }

    @Override
    public @NotNull Double get(@NotNull LivingEntity livingEntity) {
        return function.apply(livingEntity);
    }

    @Override
    public void describe(@NotNull AbilityLorePart componentable) {
        getType().describe(this, componentable);
    }
}
