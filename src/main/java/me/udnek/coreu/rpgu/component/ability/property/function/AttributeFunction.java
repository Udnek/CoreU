package me.udnek.coreu.rpgu.component.ability.property.function;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AttributeFunction implements RPGUPropertyFunction<LivingEntity, Double> {

    protected CustomAttribute attribute;
    protected RPGUPropertyFunction<LivingEntity, Double> function;

    public AttributeFunction(@NotNull CustomAttribute attribute, @NotNull RPGUPropertyFunction<LivingEntity, Double> function){
        this.attribute = attribute;
        this.function = function;
    }

    public AttributeFunction(@NotNull CustomAttribute attribute, double base){
        this(attribute, PropertyFunctions.CONSTANT(base));
    }

    @Override
    public @NotNull Double getBase() {return function.getBase();}

    @Override
    public boolean isConstant() {
        return function.isConstant();
    }

    @Override
    public boolean isZeroConstant() {
        return function.isZeroConstant();
    }

    public @NotNull Double getFunction() {
        return function.getBase();
    }

    @Override
    public @NotNull Double apply(@NotNull LivingEntity livingEntity) {
        return attribute.calculateWithBase(livingEntity, function.apply(livingEntity));
    }

    @Override
    public @NotNull MultiLineDescription describeWithModifier(@NotNull Function<Double, Double> modifier) {
        if (PropertyFunctions.IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("attr(")).add(Component.text(")"));
        return function.describeWithModifier(modifier);
    }
}


















