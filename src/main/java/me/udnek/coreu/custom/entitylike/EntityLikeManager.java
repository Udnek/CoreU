package me.udnek.coreu.custom.entitylike;

import com.google.common.base.Preconditions;
import me.udnek.coreu.util.TickingTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public abstract class EntityLikeManager<
        Real,
        Type extends EntityLikeType<Real>,
        Entity extends EntityLike<Real, ? extends Type>> extends TickingTask{

    protected List<Entity> loaded = new ArrayList<>();
    protected List<Entity> toUnloadTickets = new ArrayList<>();

    protected abstract boolean equals(@NotNull Real r1, @NotNull Real r2);

    public List<Entity> getAllLoaded(){
        return new ArrayList<>(loaded);
    }

    public @Nullable Entity getTicking(@NotNull Real real){
        for (Entity entity : loaded) {
            if (!equals(entity.getReal(), real)) continue;
            return entity;
        }
        return null;
    }

    public @NotNull Entity getTickingOrException(@NotNull Real real){
        Entity ticking = getTicking(real);
        Preconditions.checkArgument(ticking != null, "Ticking not fount: " + real);
        return ticking;
    }

    public boolean isTickingLoaded(@NotNull Real real){
        return getTicking(real) != null;
    }

    public void loadAny(@NotNull Type type, @NotNull Real real){
        type.load(real);
        if (type instanceof EntityLikeTickingType<?, ?>){
            Entity newClass = ((EntityLikeTickingType<Real, Entity>) type).createNewClass();
            newClass.load(real);
            loaded.add(newClass);
        }
    }

    public void unloadAny(@NotNull Type type, @NotNull Real real){
        if (type instanceof EntityLikeTickingType<?, ?>){
            Entity ticking = getTicking(real);
            if (ticking != null) unloadTicking(getTickingOrException(real));
        } else {
            type.unload(real);
        }
    }

    public void unloadTicking(@NotNull Entity entity){
        Preconditions.checkArgument(!toUnloadTickets.contains(entity), "TickingEntity already ticked to be unloaded: " + entity);
        toUnloadTickets.add(entity);
    }


    @Override
    public void run() {
        for (Entity entity : toUnloadTickets) {
            Type type = entity.getType();
            type.unload(entity.getReal());
            if (type instanceof EntityLikeTickingType<?, ?>){
                getTicking(entity.getReal()).unload();
            }
        }
        loaded.removeAll(toUnloadTickets);
        toUnloadTickets.clear();
        for (Entity entity : loaded) {
            if (entity.isValid()) entity.tick();
            else toUnloadTickets.add(entity);
        }
    }

    @Override
    public int getDelay() {return 1;}
}
