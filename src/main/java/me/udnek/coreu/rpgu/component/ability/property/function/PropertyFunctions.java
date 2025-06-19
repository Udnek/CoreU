package me.udnek.coreu.rpgu.component.ability.property.function;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class PropertyFunctions {

    public static final boolean IS_DEBUG = false;

    public static @NotNull AttributeFunction ATTRIBUTE(@NotNull CustomAttribute attribute, @NotNull RPGUPropertyFunction<LivingEntity, Double> base){
        return new AttributeFunction(attribute, base);
    }
    public static @NotNull AttributeFunction ATTRIBUTE(@NotNull CustomAttribute attribute, double base){
        return new AttributeFunction(attribute, base);
    }

    public static <Context, Value extends Number> @NotNull RPGUPropertyFunction<Context, Value> CONSTANT(@NotNull Value value){
        return new RPGUPropertyFunction<>() {

            @Override
            public boolean isZeroConstant() {return value.doubleValue() == 0;}

            @Override
            public boolean isConstant() {return true;}

            @Override
            public @NotNull Value getBase() {
                return value;
            }

            @Override
            public @NotNull Value apply(@NotNull Context context) {
                return value;
            }

            @Override
            public @NotNull MultiLineDescription describeWithModifier(@NotNull Function<Double, Double> modifier) {
                if (IS_DEBUG) return new MultiLineDescription().add(Component.text("const(" + modifier.apply(modifier.apply(value.doubleValue())) + ")"));
                return new MultiLineDescription().add(Component.text(Utils.roundToTwoDigits(modifier.apply(value.doubleValue()))));
            }

        };
    }
    public static <Context> @NotNull RPGUPropertyFunction<Context, Integer> CEIL(RPGUPropertyFunction<Context, Double> function){
        return new RPGUPropertyFunction<>() {
            @Override
            public boolean isConstant() {return function.isConstant();}

            @Override
            public boolean isZeroConstant() {return function.isZeroConstant();}

            public @NotNull Integer getBase() {
                return (int) Math.ceil(function.getBase());
            }

            @Override
            public @NotNull Integer apply(@NotNull Context context) {
                return (int) Math.ceil(function.apply(context));
            }

            @Override
            public @NotNull MultiLineDescription describeWithModifier(@NotNull Function<Double, Double> modifier) {
                if (IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("ceil(")).add(Component.text(")"));
                return function.describeWithModifier(modifier);
            }
        };
    }
    public static <Context> @NotNull RPGUPropertyFunction<Context, Integer> FLOOR(RPGUPropertyFunction<Context, Double> function){
        return new RPGUPropertyFunction<>() {

            @Override
            public boolean isZeroConstant() {return function.isZeroConstant();}

            @Override
            public boolean isConstant() {return function.isConstant();}

            public @NotNull Integer getBase() {
                return (int) Math.floor(function.getBase());
            }

            @Override
            public @NotNull Integer apply(@NotNull Context context) {
                return (int) Math.floor(function.apply(context));
            }

            @Override
            public @NotNull MultiLineDescription describeWithModifier(@NotNull Function<Double, Double> modifier) {
                if (IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("floor(")).add(Component.text(")"));
                return function.describeWithModifier(modifier);
            }
        };
    }
}
