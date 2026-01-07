package me.udnek.coreu.custom.entitylike;

import org.jetbrains.annotations.NotNull;

public interface EntityLike<Real, Type extends EntityLikeTickingType<?, ?>> {
    void load(@NotNull Real real);
    void unload();
    void tick();

    /**
     * @return false, if entityLike should be unloaded next tick
     */
    boolean isValid();
    @NotNull Real getReal();
    @NotNull Type getType();
}
