package me.udnek.coreu.custom.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@org.jspecify.annotations.NullMarked public abstract class CustomEvent extends Event{

    protected static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList(){return HANDLER_LIST;}
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
