package me.udnek.coreu.rpgu.component.ability.property.function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked public class Modifiers{

    public static <Context extends Number> Function<Context, Double> TICKS_TO_SECONDS(){
        return new Function<>() {
            @Override
            public Double apply(@NonNull Context context) {
                return context.doubleValue() / 20d;
            }
        };
    }
}
