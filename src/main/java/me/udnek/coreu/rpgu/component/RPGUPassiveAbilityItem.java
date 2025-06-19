package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.CustomItemComponent;
import me.udnek.coreu.rpgu.component.ability.passive.RPGUItemPassiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

public class RPGUPassiveAbilityItem extends AbstractComponentHolder<RPGUPassiveAbilityItem, RPGUItemPassiveAbility<?>> implements CustomItemComponent {

    public static final RPGUPassiveAbilityItem DEFAULT = new RPGUPassiveAbilityItem(){
        @Override
        public @NotNull CustomComponentMap<RPGUPassiveAbilityItem, RPGUItemPassiveAbility<?>> getComponents() {
            return CustomComponentMap.immutableAlwaysEmpty();
        }
    };

    @Override
    public @NotNull CustomComponentType<? extends CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return RPGUComponents.PASSIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder) {
        for (RPGUItemPassiveAbility<?> component : getComponents()) component.getLore(loreBuilder);
    }
}
