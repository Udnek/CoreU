package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface RPGUAbilityProperty<Context, Value> extends CustomComponent<RPGUItemAbility<?>>{
    @NonNull Value getBase();
    @NonNull Value get(@NonNull Context context);
    void describe(AbilityLorePart componentable);
}
