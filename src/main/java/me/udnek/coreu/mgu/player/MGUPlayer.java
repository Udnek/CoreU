package me.udnek.coreu.mgu.player;

import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.Resettable;
import me.udnek.coreu.mgu.component.MGUComponents;
import me.udnek.coreu.mgu.component.MGUPlayerData;
import me.udnek.coreu.mgu.component.MGUPlayerDataHolder;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MGUPlayer extends ComponentHolder<MGUPlayer, CustomComponent<MGUPlayer>>, Resettable {

    @NotNull MGUGameInstance getGame();

    @NotNull Player getPlayer();

    default void unregister(){
        MGUManager.get().unregisterPlayer(this);
    }

    default @NotNull CustomComponentMap<MGUPlayerDataHolder, MGUPlayerData> getData(){
        return getComponents().getOrCreateDefault(MGUComponents.PLAYER_DATA).getComponents();
    }

    @Override
    default void reset(){
        getComponents().getOrDefault(MGUComponents.PLAYER_DATA).reset();
    }
}
