package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.instance.EntityPlacingItem;
import me.udnek.coreu.custom.component.instance.MiddleClickableEntity;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.item.ConstructableCustomItem;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked
public abstract class ConstructableCustomEntityType<T extends Entity> extends AbstractRegistrableComponentable<CustomEntityType> implements CustomEntityType{

    private @Nullable CustomItem spawnEgg = null;

    public abstract EntityType getVanillaType();

    @Override
    public void initialize(Plugin plugin) {
        super.initialize(plugin);
        getComponents().set(new MiddleClickableEntity(getSpawnEgg()));
    }

    @MustBeInvokedByOverriders
    protected T spawnNewEntity(Location location){
        //noinspection unchecked
        return (T) location.getWorld().spawnEntity(location, getVanillaType());
    }

    public final T spawn(Location location) {
        T entity = spawnNewEntity(location);
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        persistentDataContainer.set(PDC_NAMESPACE, PersistentDataType.STRING, getId());

        CustomEntityManager.getInstance().loadAny(this, entity);
        return entity;
    }

    protected CustomItem getSpawnEgg() {
        if (spawnEgg == null) {
            spawnEgg = CustomRegistries.ITEM.register(CoreU.getInstance(), new AbstractSpawnEgg<>(this));
        }
        return spawnEgg;

    }

    public static class AbstractSpawnEgg<V extends Entity> extends ConstructableCustomItem {

        private final ConstructableCustomEntityType<V> entity;

        public AbstractSpawnEgg(ConstructableCustomEntityType<V> entity) {
            System.out.println(entity);
            this.entity = entity;
        }

        @Override
        public String getRawId() {
            return entity.getRawId() + "_abstract_spawn_egg";
        }

        @Override
        public @Nullable TranslatableThing getTranslations() {
            return TranslatableThing.ofEngAndRu("Abstract Spawn Egg", "Абстрактное яйцо спавна");
        }

        @Override
        public void initializeComponents() {
            super.initializeComponents();
            getComponents().set(new EntityPlacingItem(entity));
        }
    }
}
