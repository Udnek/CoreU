package me.udnek.coreu.mgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.mgu.Resettable;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.jetbrains.annotations.NotNull;

public class MGUPlayerDataHolder extends AbstractComponentHolder<MGUPlayerDataHolder> implements CustomComponent<MGUPlayer>, Resettable {

    public static final MGUPlayerDataHolder DEFAULT = new MGUPlayerDataHolder(){
        @Override
        public @NotNull CustomComponentMap<MGUPlayerDataHolder> getComponents() {
            return CustomComponentMap.immutableEmpty();
        }
    };

    public MGUPlayerDataHolder(){}

    @Override
    public void reset() {
        for (CustomComponent<? super MGUPlayerDataHolder> component : getComponents()) {
            if (component instanceof Resettable resettable) resettable.reset();
        }
    }

    @Override
    public @NotNull CustomComponentType<? super MGUPlayer, ? extends CustomComponent<? super MGUPlayer>> getType() {
        return MGUComponents.PLAYER_DATA;
    }
}
