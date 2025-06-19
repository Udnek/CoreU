package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.rpgu.component.ability.property.function.RPGUPropertyFunction;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAbilityProperty<Context, Value> implements RPGUAbilityProperty<Context, Value> {

    protected @NotNull RPGUPropertyFunction<Context, Value> function;

    public AbstractAbilityProperty(@NotNull RPGUPropertyFunction<Context, Value> function){
        this.function = function;
    }

    public @NotNull RPGUPropertyFunction<Context, Value> getFunction() {
        return function;
    }

    @Override
    public @NotNull Value getBase() {return function.getBase();}

}
