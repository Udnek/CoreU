package me.udnek.coreu.custom.entitylike;

import org.jspecify.annotations.NullMarked;

@NullMarked public  interface EntityLike<Real, Type extends EntityLikeTickingType<?, ?>>{
    void load(Real real);
    void unload();
    void tick();

    /**
     * @return false, if entityLike should be unloaded next tick
     */
    boolean isValid();
    Real getReal();
    Type getType();
}
