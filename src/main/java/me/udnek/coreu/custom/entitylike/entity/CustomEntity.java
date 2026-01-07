package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.entitylike.EntityLike;
import org.bukkit.entity.Entity;

public interface CustomEntity extends EntityLike<Entity, CustomTickingEntityType<?>> {
    void remove();
}
