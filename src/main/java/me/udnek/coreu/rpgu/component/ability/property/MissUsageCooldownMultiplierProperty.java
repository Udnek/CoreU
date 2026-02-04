package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public class MissUsageCooldownMultiplierProperty implements RPGUAbilityProperty<Player, Double>{

    protected double base;

    public MissUsageCooldownMultiplierProperty(Double base) {
        this.base = base;
    }

    @Override
    public Double getBase() {
        return base;
    }

    @Override
    public Double get(Player player) {
        return getBase();
    }

    @Override
    public void describe(AbilityLorePart componentable) {}

    @Override
    public CustomComponentType<? super RPGUItemAbility<?>, ? extends CustomComponent<? super RPGUItemAbility<?>>> getType() {
        return RPGUComponents.ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER;
    }
}
