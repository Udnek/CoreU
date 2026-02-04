package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.rpgu.component.ability.property.function.AttributeFunction;
import me.udnek.coreu.rpgu.component.ability.property.function.RPGUPropertyFunction;
import me.udnek.coreu.rpgu.component.ability.property.type.AttributeBasedPropertyType;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.bukkit.entity.LivingEntity;

@org.jspecify.annotations.NullMarked public class AttributeBasedProperty implements RPGUAbilityProperty<LivingEntity, Double>{

    protected AttributeBasedPropertyType type;
    protected RPGUPropertyFunction<LivingEntity, Double> function;

    public AttributeBasedProperty(AttributeFunction function, AttributeBasedPropertyType type) {
        this.function = function;
        this.type = type;
    }

    public AttributeBasedProperty(double base, AttributeBasedPropertyType type) {
        this(new AttributeFunction(type.getAttribute(), base), type);
    }

    @Override
    public AttributeBasedPropertyType getType() {return type;}

    public RPGUPropertyFunction<LivingEntity, Double> getFunction() {
        return function;
    }

    @Override
    public Double getBase() {
        return function.getBase();
    }

    @Override
    public Double get(LivingEntity livingEntity) {
        return function.apply(livingEntity);
    }

    @Override
    public void describe(AbilityLorePart componentable) {
        getType().describe(this, componentable);
    }
}
