package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MissUsageCooldownMultiplierProperty implements RPGUAbilityProperty<Player, Double> {

    protected double base;

    public MissUsageCooldownMultiplierProperty(@NotNull Double base) {
        this.base = base;
    }

    @Override
    public @NotNull Double getBase() {
        return base;
    }

    @Override
    public @NotNull Double get(@NotNull Player player) {
        return getBase();
    }

    @Override
    public void describe(@NotNull AbilityLorePart componentable) {}

    @Override
    public @NotNull CustomComponentType<? extends RPGUItemAbility<?>, ? extends CustomComponent<RPGUItemAbility<?>>> getType() {
        return RPGUComponents.ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER;
    }
}
