package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.jetbrains.annotations.NotNull;

public interface RPGUAbilityProperty<Context, Value> extends CustomComponent<RPGUItemAbility<?>> {
    @NotNull Value getBase();
    @NotNull Value get(@NotNull Context context);
    void describe(@NotNull AbilityLorePart componentable);
}
