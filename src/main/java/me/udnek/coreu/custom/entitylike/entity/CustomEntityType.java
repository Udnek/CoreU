package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.entitylike.EntityLikeType;
import me.udnek.coreu.custom.entitylike.block.constructabletype.DisplayBasedConstructableBlockType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface CustomEntityType extends EntityLikeType<Entity>, ComponentHolder<CustomEntityType> {


    CustomEntityType BLOCK_DISPLAY = CustomRegistries.ENTITY_TYPE.register(CoreU.getInstance(), new DisplayBasedConstructableBlockType.DisplayEntity());


    NamespacedKey PDC_NAMESPACE = new NamespacedKey(CoreU.getInstance(), "custom_entity_type");

    static @Nullable CustomEntityType get(@NotNull String id){
        return CustomRegistries.ENTITY_TYPE.get(id);
    }

    static @Nullable CustomEntityType get(@NotNull Entity entity) {
        String id = entity.getPersistentDataContainer().get(CustomEntityType.PDC_NAMESPACE, PersistentDataType.STRING);
        return id == null ? null : CustomEntityType.get(id);
    }


    static @Nullable CustomEntity getTicking(@NotNull Entity entity){
        return CustomEntityManager.getInstance().getTicking(entity);
    }

    static boolean isCustom(@NotNull Entity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        return dataContainer.has(CustomEntityType.PDC_NAMESPACE);
    }

    static void consumeIfCustom(@NotNull Entity entity, @NotNull Consumer<@NotNull CustomEntityType> consumer){
        CustomEntityType customEntityType = get(entity);
        if (customEntityType != null) consumer.accept(customEntityType);
    }

    default CustomEntityType getIfThis(@NotNull Entity entity) {
        CustomEntityType customEntity = get(entity);
        return customEntity != this ? null : this;
    }
    @NotNull Entity spawn(@NotNull Location location);

}
