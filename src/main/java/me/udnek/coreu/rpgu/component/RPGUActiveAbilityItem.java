package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.CustomItemComponent;
import me.udnek.coreu.rpgu.component.ability.active.RPGUItemActiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

public class RPGUActiveAbilityItem extends AbstractComponentHolder<RPGUActiveAbilityItem, RPGUItemActiveAbility<?>> implements CustomItemComponent {

    public static final RPGUActiveAbilityItem DEFAULT = new RPGUActiveAbilityItem(){
        @Override
        public @NotNull CustomComponentMap<RPGUActiveAbilityItem, RPGUItemActiveAbility<?>> getComponents() {
            return CustomComponentMap.immutableAlwaysEmpty();
        }
    };

    @Override
    public @NotNull CustomComponentType<? extends CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return RPGUComponents.ACTIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder) {
        for (RPGUItemActiveAbility<?> component : getComponents()) component.getLore(loreBuilder);
    }
}
