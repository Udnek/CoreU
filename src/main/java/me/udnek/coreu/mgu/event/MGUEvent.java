package me.udnek.coreu.mgu.event;

import me.udnek.coreu.custom.event.CustomEvent;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import org.jetbrains.annotations.NotNull;

public class MGUEvent extends CustomEvent {

    protected MGUGameInstance game;

    public MGUEvent(@NotNull MGUGameInstance game){
        this.game = game;
    }

    public @NotNull MGUGameInstance getGame() {
        return game;
    }
}
