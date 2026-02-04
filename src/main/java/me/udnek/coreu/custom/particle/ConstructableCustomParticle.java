package me.udnek.coreu.custom.particle;

import me.udnek.coreu.CoreU;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.UnknownNullability;

@org.jspecify.annotations.NullMarked public abstract class ConstructableCustomParticle<EntityT extends Entity> implements CustomParticle{
    public int frameNumber;
    protected @UnknownNullability BukkitRunnable task;
    protected @UnknownNullability EntityT display;
    protected @UnknownNullability Location location;

    abstract public @Positive int getFramesAmount();
    abstract public EntityType getType();

    public @Positive int getFrameTime() {return 1;}

    @Override
    public void play(Location location) {
        this.location = location.clone();
        frameNumber = 0;
        spawn();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (frameNumber == getFramesAmount()) {
                    assert display != null;
                    display.remove();
                    cancel();
                }
                ConstructableCustomParticle.this.nextFrame();
                frameNumber++;
            }
        };
        task.runTaskTimer(CoreU.getInstance(), 0, getFrameTime());
    }


    @Override
    public void stop() {
        if (task != null) task.cancel();
        if (display != null) display.remove();
    }

    abstract protected void nextFrame();

    @SuppressWarnings("unchecked")
    @MustBeInvokedByOverriders
    protected void spawn(){
        assert location != null;
        display = (EntityT) location.getWorld().spawnEntity(location, getType());
    }
}
