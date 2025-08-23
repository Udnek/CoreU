package me.udnek.coreu.mgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.mgu.Resettable;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.jetbrains.annotations.NotNull;

public class MGUPlayerDataHolder extends AbstractComponentHolder<MGUPlayerDataHolder, MGUPlayerData> implements CustomComponent<MGUPlayer>, Resettable {

    public static final MGUPlayerDataHolder DEFAULT = new MGUPlayerDataHolder(){
        @Override
        public @NotNull CustomComponentMap<MGUPlayerDataHolder, MGUPlayerData> getComponents() {
            return CustomComponentMap.immutableAlwaysEmpty();
        }
    };

    public MGUPlayerDataHolder(){}

    public void clear(){
        for (MGUPlayerData component : getComponents()) getComponents().remove(component.getType());
    }

    @Override
    public void reset() {
        for (MGUPlayerData component : getComponents()) component.reset();
    }

    @Override
    public @NotNull CustomComponentType<? extends MGUPlayer, ? extends CustomComponent<MGUPlayer>> getType() {
        return MGUComponents.PLAYER_DATA;
    }
}
