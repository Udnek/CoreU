package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.component.instance.MiddleClickableEntity;
import me.udnek.coreu.custom.item.instace.UniversalSpawnEgg;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@org.jspecify.annotations.NullMarked
public abstract class ConstructableCustomEntityType<T extends Entity> extends AbstractRegistrableComponentable<CustomEntityType> implements CustomEntityType{

    public abstract EntityType getVanillaType();

    @Override
    public void initialize(Plugin plugin) {
        super.initialize(plugin);

    }

    @Override
    public void globalInitialization() {
        super.globalInitialization();
        if (getSpawnEgg() != null) getComponents().set(new MiddleClickableEntity(getSpawnEgg()));
    }

    @MustBeInvokedByOverriders
    protected T spawnNewEntity(Location location){
        //noinspection unchecked
        return (T) location.getWorld().spawnEntity(location, getVanillaType());
    }

    public final  T spawn(Location location) {
        T entity = spawnNewEntity(location);
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        persistentDataContainer.set(PDC_NAMESPACE, PersistentDataType.STRING, getId());

        CustomEntityManager.getInstance().loadAny(this, entity);
        return entity;
    }

    protected @Nullable ItemStack getSpawnEgg() {
        return UniversalSpawnEgg.getWithEntityId(getId());
    }
}
