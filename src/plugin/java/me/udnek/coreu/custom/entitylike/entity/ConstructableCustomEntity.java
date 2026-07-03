package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.custom.entitylike.AbstractEntityLike;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.NonNull;


@org.jspecify.annotations.NullMarked public abstract class ConstructableCustomEntity<VType extends Entity> extends AbstractEntityLike<Entity, CustomTickingEntityType<?>>implements CustomEntity{

    protected VType entity;

    @Override
    public boolean isValid() {
        return entity.isValid();
    }

    @Override
    public void load(Entity entity) {
        this.entity = (VType) entity;
    }

    @Override
    public @NonNull VType getReal() {
        return entity;
    }

    @MustBeInvokedByOverriders
    @Override
    public void remove() {
        entity.remove();
        CustomEntityManager.getInstance().unloadTicking(this);
    }
}
