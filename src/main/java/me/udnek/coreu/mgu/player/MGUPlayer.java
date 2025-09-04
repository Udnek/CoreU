package me.udnek.coreu.mgu.player;

import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.Resettable;
import me.udnek.coreu.mgu.component.MGUComponents;
import me.udnek.coreu.mgu.component.MGUPlayerDataHolder;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MGUPlayer extends ComponentHolder<MGUPlayer>, Resettable {

    @NotNull MGUGameInstance getGame();

    @NotNull Player getPlayer();

    default void unregister(){
        MGUManager.get().unregisterPlayer(this);
    }

    default @NotNull CustomComponentMap<MGUPlayerDataHolder> getData(){
        return getComponents().getOrCreateDefault(MGUComponents.PLAYER_DATA).getComponents();
    }

    @Override
    default void reset(){
        getComponents().getOrDefault(MGUComponents.PLAYER_DATA).reset();
    }
}
