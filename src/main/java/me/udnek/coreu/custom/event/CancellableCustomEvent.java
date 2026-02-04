package me.udnek.coreu.custom.event;

import org.bukkit.event.Cancellable;

@org.jspecify.annotations.NullMarked public abstract class CancellableCustomEvent extends CustomEvent implements Cancellable{

    protected boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
