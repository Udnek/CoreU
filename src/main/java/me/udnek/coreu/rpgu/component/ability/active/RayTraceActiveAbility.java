package me.udnek.coreu.rpgu.component.ability.active;

import com.destroystokyo.paper.ParticleBuilder;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface RayTraceActiveAbility<Context> extends RPGUItemActiveAbility<Context> {

    default @Nullable Collection<LivingEntity> findLivingEntitiesInRayTraceRadius(@NotNull LivingEntity livingEntity, @Nullable ParticleBuilder particle){
        RayTraceResult rayTraceResult = Utils.rayTraceBlockOrEntity(livingEntity, getComponents().getOrException(RPGUComponents.ABILITY_CAST_RANGE).get(livingEntity));
        if (rayTraceResult == null) return null;
        Location location = rayTraceResult.getHitPosition().toLocation(livingEntity.getWorld());
        final double radius = getComponents().getOrException(RPGUComponents.ABILITY_AREA_OF_EFFECT).get(livingEntity);
        Collection<LivingEntity> nearbyLivingEntities = Utils.findLivingEntitiesInRadiusIntersects(location, radius);
        if (particle != null) showRadius(particle.location(location), radius);
        return nearbyLivingEntities;
    }

    default @Nullable Collection<LivingEntity> findLivingEntitiesInRayTraceRadius(@NotNull LivingEntity player){
        return findLivingEntitiesInRayTraceRadius(player, null);
    }

    default void showRadius(@NotNull ParticleBuilder particleBuilder, double size){
        Utils.particleCircle(particleBuilder, size);
    }
}
