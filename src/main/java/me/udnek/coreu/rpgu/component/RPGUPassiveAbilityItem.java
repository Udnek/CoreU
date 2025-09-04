package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.rpgu.component.ability.passive.RPGUItemPassiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RPGUPassiveAbilityItem extends AbstractComponentHolder<RPGUPassiveAbilityItem> implements LoreProvidingItemComponent {

    public static final RPGUPassiveAbilityItem DEFAULT = new RPGUPassiveAbilityItem(){
        @Override
        public @NotNull CustomComponentMap<RPGUPassiveAbilityItem> getComponents() {
            return CustomComponentMap.immutableEmpty();
        }
    };

    public @NotNull List<RPGUItemPassiveAbility> getAbilities(){
        return getComponents().getAllTyped(RPGUItemPassiveAbility.class);
    }

    @Override
    public @NotNull CustomComponentType<? super CustomItem, ? extends CustomComponent<? super CustomItem>> getType() {
        return RPGUComponents.PASSIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder) {
        getAbilities().forEach(component -> component.getLore(loreBuilder));
    }
}
