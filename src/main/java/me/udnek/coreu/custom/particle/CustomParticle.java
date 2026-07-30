package me.udnek.coreu.custom.particle;

import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface CustomParticle{
    void play(Location location);
    void stop();
}
