package me.udnek.coreu.custom.entitylike;

import org.jspecify.annotations.NonNull;

@org.jspecify.annotations.NullMarked public  interface EntityLikeTickingType<Real, Entity extends EntityLike<?, ?>> extends EntityLikeType<Real>{
    @NonNull Entity createNewClass();
}
