package me.udnek.coreu.rpgu.component.ability.property;

import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import me.udnek.coreu.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@org.jspecify.annotations.NullMarked public class CastTimeProperty implements RPGUAbilityProperty<Player, Integer>{

    protected int time;

    public CastTimeProperty(int base){
        this.time = base;
    }

    @Override
    public Integer getBase() {
        return time;
    }

    @Override
    public Integer get(Player player) {
        return getBase();
    }

    @Override
    public void describe(AbilityLorePart componentable) {
        componentable.addAbilityProperty(Component.translatable("rpgu_ability_property.coreu.cast_time", Component.text(Utils.roundToTwoDigits(getBase()/20d))));
    }

    @Override
    public CustomComponentType<RPGUItemAbility<?>, ?> getType() {
        return RPGUComponents.ABILITY_CAST_TIME;
    }
}
