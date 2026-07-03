package me.udnek.coreu.custom.entitylike;

import me.udnek.coreu.custom.registry.Registrable;
import org.jspecify.annotations.NonNull;

@org.jspecify.annotations.NullMarked public  interface EntityLikeType<Real> extends Registrable{
    void load(@NonNull Real real);
    void unload(@NonNull Real real);
}
