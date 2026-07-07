package me.udnek.coreu.custom.entitylike;

import com.google.common.base.Preconditions;
import me.udnek.coreu.util.TickingTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public abstract class EntityLikeManager<
        Real,
        Type extends EntityLikeType<Real>,
        Entity extends EntityLike<Real, ? extends Type>> extends TickingTask{

    protected List<Entity> loaded = new ArrayList<>();
    protected List<Entity> toUnloadTickets = new ArrayList<>();

    protected abstract boolean equals(@NonNull Real r1, @NonNull Real r2);

    public List<Entity> getAllLoaded(){
        return new ArrayList<>(loaded);
    }

    public @Nullable Entity getTicking(@NonNull Real real){
        for (Entity entity : loaded) {
            if (!equals(entity.getReal(), real)) continue;
            return entity;
        }
        return null;
    }

    public @NonNull Entity getTickingOrException(@NonNull Real real){
        Entity ticking = getTicking(real);
        Preconditions.checkArgument(ticking != null, "Ticking not fount: " + real);
        return ticking;
    }

    public boolean isTickingLoaded(@NonNull Real real){
        return getTicking(real) != null;
    }

    public void loadAny(@NonNull Type type, @NonNull Real real){
        type.load(real);
        if (type instanceof EntityLikeTickingType<?, ?>){
            Entity newClass = ((EntityLikeTickingType<Real, Entity>) type).createNewClass();
            newClass.load(real);
            loaded.add(newClass);
        }
    }

    public void unloadAny(@NonNull Type type, @NonNull Real real){
        if (type instanceof EntityLikeTickingType<?, ?>){
            Entity ticking = getTicking(real);
            if (ticking != null) unloadTicking(getTickingOrException(real));
        } else {
            type.unload(real);
        }
    }

    public void unloadTicking(@NonNull Entity entity){
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
