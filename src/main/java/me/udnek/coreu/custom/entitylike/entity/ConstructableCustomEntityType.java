package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

@org.jspecify.annotations.NullMarked public abstract class ConstructableCustomEntityType<T extends Entity> extends AbstractRegistrable implements CustomEntityType{

    private CustomComponentMap<CustomEntityType> components = null;

    @Override
    public CustomComponentMap<CustomEntityType> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }

    public abstract EntityType getVanillaType();

    @MustBeInvokedByOverriders
    protected @NotNull T spawnNewEntity(Location location){
        return (T) location.getWorld().spawnEntity(location, getVanillaType());
    }

    public final @NotNull T spawn(Location location) {
        T entity = spawnNewEntity(location);
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        persistentDataContainer.set(PDC_NAMESPACE, PersistentDataType.STRING, getId());

        CustomEntityManager.getInstance().loadAny(this, entity);
        return entity;
    }
}
