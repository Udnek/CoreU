package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class ConstructableCustomEntityType<T extends Entity> extends AbstractRegistrable implements CustomEntityType {

    private CustomComponentMap<CustomEntityType, CustomComponent<CustomEntityType>> components = null;

    @Override
    public @NotNull CustomComponentMap<CustomEntityType, CustomComponent<CustomEntityType>> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }

    public abstract @NotNull EntityType getVanillaType();

    @MustBeInvokedByOverriders
    public @NotNull T spawnNewEntity(@NotNull Location location){
        return (T) location.getWorld().spawnEntity(location, getVanillaType());
    }

    public final @NotNull T spawn(@NotNull Location location) {
        T entity = spawnNewEntity(location);
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        persistentDataContainer.set(PDC_NAMESPACE, PersistentDataType.STRING, getId());
        new BukkitRunnable() {
            @Override
            public void run() {
                CustomEntityManager.getInstance().loadAny(ConstructableCustomEntityType.this, entity);
            }
        }.runTask(CoreU.getInstance());
        return entity;
    }
}
