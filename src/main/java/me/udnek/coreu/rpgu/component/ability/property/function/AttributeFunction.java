package me.udnek.coreu.rpgu.component.ability.property.function;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;

import java.util.function.Function;

@org.jspecify.annotations.NullMarked public class AttributeFunction implements RPGUPropertyFunction<LivingEntity, Double>{

    protected CustomAttribute attribute;
    protected RPGUPropertyFunction<LivingEntity, Double> function;

    public AttributeFunction(CustomAttribute attribute, RPGUPropertyFunction<LivingEntity, Double> function){
        this.attribute = attribute;
        this.function = function;
    }

    public AttributeFunction(CustomAttribute attribute, double base){
        this(attribute, PropertyFunctions.CONSTANT(base));
    }

    @Override
    public Double getBase() {return function.getBase();}

    @Override
    public boolean isConstant() {
        return function.isConstant();
    }

    @Override
    public boolean isZeroConstant() {
        return function.isZeroConstant();
    }

    public Double getFunction() {
        return function.getBase();
    }

    @Override
    public Double apply(LivingEntity livingEntity) {
        return attribute.calculateWithBase(livingEntity, function.apply(livingEntity));
    }

    @Override
    public MultiLineDescription describeWithModifier(Function<Double, Double> modifier) {
        if (PropertyFunctions.IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("attr(")).add(Component.text(")"));
        return function.describeWithModifier(modifier);
    }
}


















