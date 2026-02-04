package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.entitylike.EntityLikeTickingType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  interface CustomTickingEntityType<CEntity extends CustomEntity> extends EntityLikeTickingType<Entity, CEntity>, CustomEntityType{

    default @NotNull CEntity spawnAndGet(Location location){
        Entity spawned = spawn(location);
        return (CEntity) CustomEntityManager.getInstance().getTicking(spawned);
    }

    default @Nullable CEntity getIsThis(Entity entity) {
        CustomEntity customEntity = CustomEntityType.getTicking(entity);
        return customEntity == null || customEntity.getType() != this ? null : (CEntity) customEntity;
    }

}
