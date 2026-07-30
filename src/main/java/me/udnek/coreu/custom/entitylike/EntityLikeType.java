package me.udnek.coreu.custom.entitylike;

import me.udnek.coreu.custom.registry.Registrable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface EntityLikeType<Real> extends Registrable{
    void load(@NonNull Real real);
    void unload(@NonNull Real real);
}
