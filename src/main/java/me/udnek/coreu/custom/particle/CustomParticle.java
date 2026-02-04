package me.udnek.coreu.custom.particle;

import org.bukkit.Location;

@org.jspecify.annotations.NullMarked public  interface CustomParticle{
    void play(Location location);
    void stop();
}
