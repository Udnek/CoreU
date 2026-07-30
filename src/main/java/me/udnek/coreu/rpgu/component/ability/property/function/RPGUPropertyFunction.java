package me.udnek.coreu.rpgu.component.ability.property.function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked public  interface RPGUPropertyFunction<Context, Value> extends Function<Context, Value>{
    @NonNull Value getBase();
    @Override
    @NonNull Value apply(@NonNull Context context);
    boolean isConstant();
    boolean isZeroConstant();
    default MultiLineDescription describe(){
        return describeWithModifier(Function.identity());
    }
    MultiLineDescription describeWithModifier(Function<Double, Double> modifier);
}
