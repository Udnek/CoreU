package me.udnek.coreu.rpgu.component.ability.property.function;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@org.jspecify.annotations.NullMarked public class PropertyFunctions{

    public static final boolean IS_DEBUG = false;

    public static AttributeFunction ATTRIBUTE_WITH_BASE(CustomAttribute attribute, RPGUPropertyFunction<LivingEntity, Double> base){
        return new AttributeFunction(attribute, base);
    }
    public static AttributeFunction ATTRIBUTE_WITH_BASE(CustomAttribute attribute, double base){
        return new AttributeFunction(attribute, base);
    }

    public static <Context, Value extends Number> RPGUPropertyFunction<Context, Value> CONSTANT(@NotNull Value value){
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
            public MultiLineDescription describeWithModifier(Function<Double, Double> modifier) {
                if (IS_DEBUG) return new MultiLineDescription().add(Component.text("const(" + modifier.apply(modifier.apply(value.doubleValue())) + ")"));
                return new MultiLineDescription().add(Component.text(Utils.roundToTwoDigits(modifier.apply(value.doubleValue()))));
            }

        };
    }
    public static <Context> RPGUPropertyFunction<Context, Integer> CEIL(RPGUPropertyFunction<Context, Double> function){
        return new RPGUPropertyFunction<>() {
            @Override
            public boolean isConstant() {return function.isConstant();}

            @Override
            public boolean isZeroConstant() {return function.isZeroConstant();}

            public Integer getBase() {
                return (int) Math.ceil(function.getBase());
            }

            @Override
            public Integer apply(@NotNull Context context) {
                return (int) Math.ceil(function.apply(context));
            }

            @Override
            public MultiLineDescription describeWithModifier(Function<Double, Double> modifier) {
                if (IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("ceil(")).add(Component.text(")"));
                return function.describeWithModifier(modifier);
            }
        };
    }
    public static <Context> RPGUPropertyFunction<Context, Integer> FLOOR(RPGUPropertyFunction<Context, Double> function){
        return new RPGUPropertyFunction<>() {

            @Override
            public boolean isZeroConstant() {return function.isZeroConstant();}

            @Override
            public boolean isConstant() {return function.isConstant();}

            public Integer getBase() {
                return (int) Math.floor(function.getBase());
            }

            @Override
            public Integer apply(@NotNull Context context) {
                return (int) Math.floor(function.apply(context));
            }

            @Override
            public MultiLineDescription describeWithModifier(Function<Double, Double> modifier) {
                if (IS_DEBUG) return function.describeWithModifier(modifier).addToBeginning(Component.text("floor(")).add(Component.text(")"));
                return function.describeWithModifier(modifier);
            }
        };
    }

    // base + x*perX
    public static <In> RPGUPropertyFunction<In, Double> LINEAR(RPGUPropertyFunction<In, Double> x, RPGUPropertyFunction<In, Double> base, RPGUPropertyFunction<In, Double> perX){
        return new RPGUPropertyFunction<>() {
            @Override
            public Double getBase() {
                return base.getBase() + x.getBase() * perX.getBase();
            }

            @Override
            public Double apply(@NotNull In v) {
                return base.apply(v) + x.apply(v) * perX.apply(v);
            }

            @Override
            public boolean isConstant() {
                return base.isConstant() && x.isConstant() && perX.isConstant();
            }

            @Override
            public boolean isZeroConstant() {
                return base.isZeroConstant() && x.isConstant() && perX.isConstant();
            }

            @Override
            public MultiLineDescription describeWithModifier(Function<Double, Double> modifier) {
                return MultiLineDescription.of()
                        .add(base.describeWithModifier(modifier))
                        .add(Component.text(" + "))
                        .add(perX.describeWithModifier(modifier))
                        .add(Component.text("*("))
                        .add(x.describeWithModifier(modifier))
                        .add(Component.text(")"));
            }
        };
    }

    public static <In> RPGUPropertyFunction<In, Double> LINEAR(RPGUPropertyFunction<In, Double> x, double base, double perX){
        return LINEAR(x, CONSTANT(base), CONSTANT(perX));
    }
}
