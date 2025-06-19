package me.udnek.coreu.mgu.ability;

import me.udnek.coreu.mgu.player.MGUPlayer;
import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import org.jetbrains.annotations.NotNull;

public class MGUPlayerDataHolder extends AbstractComponentHolder<MGUPlayerDataHolder, CustomComponent<MGUPlayerDataHolder>> implements CustomComponent<MGUPlayer>{

    public static final MGUPlayerDataHolder DEFAULT = new MGUPlayerDataHolder(){
        @Override
        public @NotNull CustomComponentMap<MGUPlayerDataHolder, CustomComponent<MGUPlayerDataHolder>> getComponents() {
            return CustomComponentMap.immutableAlwaysEmpty();
        }
    };

    public MGUPlayerDataHolder(){}

    public void clear(){
        for (CustomComponent<MGUPlayerDataHolder> component : getComponents()) {
            getComponents().remove(component.getType());
        }
    }

    @Override
    public @NotNull CustomComponentType<? extends MGUPlayer, ? extends CustomComponent<MGUPlayer>> getType() {
        return MGUComponents.PLAYER_DATA;
    }
}
