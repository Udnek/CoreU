package me.udnek.coreu.mgu;

import org.bukkit.Location;

@org.jspecify.annotations.NullMarked public  interface Originable{

    static Location setOrigin(Location location, Location origin){
        location.setWorld(origin.getWorld());
        location.add(origin.x(), origin.y(), origin.z());
        return location;
    }

    void setOrigin(Location origin);
}
