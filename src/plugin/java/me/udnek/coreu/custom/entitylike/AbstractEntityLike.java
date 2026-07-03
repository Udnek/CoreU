package me.udnek.coreu.custom.entitylike;

import org.bukkit.Bukkit;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@org.jspecify.annotations.NullMarked public abstract class AbstractEntityLike<Real, Type extends EntityLikeTickingType<?, ?>> implements EntityLike<Real, Type>{

    public @Positive int getTickDelay(){return 5;}

    @Override
    @MustBeInvokedByOverriders
    public void unload() {}

    public abstract void delayedTick();

    @Override
    public final void tick() {
        if (Bukkit.getCurrentTick() % getTickDelay() == 0) delayedTick();
    }
}
