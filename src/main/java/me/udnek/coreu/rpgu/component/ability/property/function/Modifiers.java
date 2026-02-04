package me.udnek.coreu.rpgu.component.ability.property.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@org.jspecify.annotations.NullMarked public class Modifiers{

    public static <Context extends Number> Function<Context, Double> TICKS_TO_SECONDS(){
        return new Function<>() {
            @Override
            public Double apply(@NotNull Context context) {
                return context.doubleValue() / 20d;
            }
        };
    }
}
