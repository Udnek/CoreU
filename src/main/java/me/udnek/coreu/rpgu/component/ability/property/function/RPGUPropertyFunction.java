package me.udnek.coreu.rpgu.component.ability.property.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface RPGUPropertyFunction<Context, Value> extends Function<Context, Value> {
    @NotNull Value getBase();
    @Override
    @NotNull Value apply(@NotNull Context context);
    boolean isConstant();
    boolean isZeroConstant();
    @NotNull default MultiLineDescription describe(){
        return describeWithModifier(Function.identity());
    }
    @NotNull MultiLineDescription describeWithModifier(@NotNull Function<Double, Double> modifier);
}
