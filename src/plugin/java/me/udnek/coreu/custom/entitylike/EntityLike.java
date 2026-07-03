package me.udnek.coreu.custom.entitylike;

import org.jspecify.annotations.NonNull;

@org.jspecify.annotations.NullMarked public  interface EntityLike<Real, Type extends EntityLikeTickingType<?, ?>>{
    void load(@NonNull Real real);
    void unload();
    void tick();

    /**
     * @return false, if entityLike should be unloaded next tick
     */
    boolean isValid();
    @NonNull Real getReal();
    @NonNull Type getType();
}
