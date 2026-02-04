package me.udnek.coreu.rpgu.component.ability.property.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@org.jspecify.annotations.NullMarked public  interface RPGUPropertyFunction<Context, Value> extends Function<Context, Value>{
    @NotNull Value getBase();
    @Override
    @NotNull Value apply(@NotNull Context context);
    boolean isConstant();
    boolean isZeroConstant();
    default MultiLineDescription describe(){
        return describeWithModifier(Function.identity());
    }
    MultiLineDescription describeWithModifier(Function<Double, Double> modifier);
}
