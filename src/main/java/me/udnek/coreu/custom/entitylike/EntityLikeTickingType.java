package me.udnek.coreu.custom.entitylike;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface EntityLikeTickingType<Real, Entity extends EntityLike<?, ?>> extends EntityLikeType<Real>{
    @NonNull Entity createNewClass();
}
