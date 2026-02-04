package me.udnek.coreu.custom.entitylike;

import org.jetbrains.annotations.NotNull;

@org.jspecify.annotations.NullMarked public  interface EntityLikeTickingType<Real, Entity extends EntityLike<?, ?>> extends EntityLikeType<Real>{
    @NotNull Entity createNewClass();
}
