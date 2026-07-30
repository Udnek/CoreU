package me.udnek.coreu.mgu.event;

import me.udnek.coreu.custom.event.CustomEvent;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import org.jspecify.annotations.NullMarked;

@NullMarked public class MGUEvent extends CustomEvent{

    protected MGUGameInstance game;

    public MGUEvent(MGUGameInstance game){
        this.game = game;
    }

    public MGUGameInstance getGame() {
        return game;
    }
}
