package me.udnek.coreu.custom.entitylike.block;

import me.udnek.coreu.custom.entitylike.AbstractEntityLike;
import org.bukkit.block.TileState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class ConstructableCustomBlockEntity<T extends TileState> extends AbstractEntityLike<TileState, CustomBlockEntityType> implements CustomBlockEntity {

    protected T tileState;

    @Override
    @MustBeInvokedByOverriders
    public void load(@NotNull TileState tileState) {
        this.tileState = (T) tileState;
    }

    @Override
    public @NotNull T getReal() {
        return tileState;
    }
}
