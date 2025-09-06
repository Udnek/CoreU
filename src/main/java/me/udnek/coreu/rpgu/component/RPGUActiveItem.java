package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.rpgu.component.ability.active.RPGUItemActiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RPGUActiveItem extends AbstractComponentHolder<RPGUActiveItem> implements LoreProvidingItemComponent {

    public static final RPGUActiveItem DEFAULT = new RPGUActiveItem(){
        @Override
        public @NotNull CustomComponentMap<RPGUActiveItem> getComponents() {
            return CustomComponentMap.immutableEmpty();
        }
    };

    public @NotNull List<RPGUItemActiveAbility> getAbilities(){
        return getComponents().getAllTyped(RPGUItemActiveAbility.class);
    }

    @Override
    public @NotNull CustomComponentType<? super CustomItem, ? extends CustomComponent<? super CustomItem>> getType() {
        return RPGUComponents.ACTIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder) {
        getAbilities().forEach(component -> component.getLore(loreBuilder));
    }
}
