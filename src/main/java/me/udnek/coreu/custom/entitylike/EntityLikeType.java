package me.udnek.coreu.custom.entitylike;

import me.udnek.coreu.custom.registry.Registrable;
import org.jetbrains.annotations.NotNull;

@org.jspecify.annotations.NullMarked public  interface EntityLikeType<Real> extends Registrable{
    void load(@NotNull Real real);
    void unload(@NotNull Real real);
}
