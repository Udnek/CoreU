package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.entitylike.EntityLike;
import org.bukkit.entity.Entity;

@org.jspecify.annotations.NullMarked public  interface CustomEntity extends EntityLike<Entity, CustomTickingEntityType<?>>{
    void remove();
}
