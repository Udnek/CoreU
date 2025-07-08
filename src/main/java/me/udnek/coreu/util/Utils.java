package me.udnek.coreu.util;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.base.Preconditions;
import me.udnek.coreu.CoreU;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

public class Utils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    // GENERAL

    public static @NotNull String roundToTwoDigits(double value){
        return DECIMAL_FORMAT.format(value);
    }

    public static <T> void consumeIfNotNull(@Nullable T object, @NotNull Consumer<@NotNull T> consumer){
        if (object != null) consumer.accept(object);
    }

    public static @NotNull TextColor mixColors(@NotNull TextColor from, @NotNull TextColor to, float progress){
        Preconditions.checkArgument(0 <= progress && progress <= 1, "Progress must be between 0 and 1, but is: " + progress);
        Vector fromV = new Vector(from.red()/255f, from.green()/255f, from.blue()/255f);
        Vector toV = new Vector(to.red()/255f, to.green()/255f, to.blue()/255f);
        fromV.multiply(1-progress).add(toV.multiply(progress));
        return TextColor.color((float) fromV.getX(), (float) fromV.getY(), (float) fromV.getZ());
    }

    // WORLD

    public static void sendBlockDamageForAllPlayers(@NotNull Location location, float startProgress) {
        sendBlockDamageForAllPlayers(location, startProgress, startProgress, 5);
    }

    public static void sendBlockDamageForAllPlayers(@NotNull Location location, float startProgress, float step, long tickRate) {
        location.getWorld().getPlayers().forEach(player ->
                        new BukkitRunnable() {
                            float progress = startProgress;
                            @Override
                            public void run() {
                                player.sendBlockDamage(location, progress, location.getBlock().hashCode());
                                progress -= step;
                                if (progress < 0 || progress > 1) {
                                    player.sendBlockDamage(location, 0, location.getBlock().hashCode());
                                    cancel();
                                }
                            }
                        }.runTaskTimer(CoreU.getInstance(), 0, tickRate));

    }

    // RAYTRACE
    public static @Nullable RayTraceResult rayTraceBlockOrEntity(@NotNull LivingEntity livingEntity, double castRange){
        return rayTraceBlockOrEntity(livingEntity, castRange, 0);
    }
    public static @Nullable RayTraceResult rayTraceBlockOrEntity(@NotNull LivingEntity livingEntity, double castRange, double raySize){
        Location location = livingEntity.getEyeLocation();
        World world = livingEntity.getWorld();

        RayTraceResult rayTraceResultBlocks = world.rayTraceBlocks(location, location.getDirection(), castRange, FluidCollisionMode.NEVER, true);

        if (rayTraceResultBlocks != null) return rayTraceResultBlocks;
        return world.rayTraceEntities(location, location.getDirection(), castRange, raySize, entity -> entity != livingEntity);
    }
    public static @Nullable Location rayTraceBlockUnder(@NotNull LivingEntity livingEntity){
        return rayTraceBlockUnder(livingEntity.getLocation());
    }
    public static @Nullable Location rayTraceBlockUnder(@NotNull Location location){
        World world = location.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(location.add(0 , 1, 0), new Vector().setY(-1), 10000, FluidCollisionMode.NEVER, true);

        if (rayTraceResult == null) return null;
        return rayTraceResult.getHitPosition().toLocation(location.getWorld());
    }

    // NEARBY
    public static @NotNull Collection<LivingEntity> findLivingEntitiesInRadius(@NotNull Location location, double radius){
        return location.getWorld().getNearbyLivingEntities(location, radius,
                livingEntity -> livingEntity.getLocation().distance(location) <= radius);
    }
    public static @NotNull Collection<LivingEntity> findLivingEntitiesInRadiusIntersects(@NotNull Location location, double radius){
        return location.getWorld().getNearbyLivingEntities(location, radius+15,
                entity -> entity.getBoundingBox().expand(radius).contains(location.toVector()));
    }

    // PARTICLE

    public static void particlePlayUntilGround(@NotNull AbstractArrow arrow, @NotNull ParticleBuilder particle){
        new BukkitRunnable() {
            public void run() {
                if (arrow.isOnGround() || !arrow.isValid()) {
                    cancel();
                }
                particle.location(arrow.getLocation());
                particle.spawn();
            }
        }.runTaskTimer(CoreU.getInstance(), 0, 1);
    }

    public static void particleDrawLine(@NotNull Particle particle, @NotNull Location from, @NotNull Location to, double space) {
        World world = from.getWorld();
        double distance = from.distance(to);
        Vector pointFrom = from.toVector();
        Vector pointTo = to.toVector();
        Vector vector = pointTo.clone().subtract(pointFrom).normalize().multiply(space);
        for (double length = 0; length < distance; pointFrom.add(vector)) {
            world.spawnParticle(particle, pointFrom.getX(), pointFrom.getY(), pointFrom.getZ(), 1);
            length += space;
        }
    }

    public static void particleCircle(@NotNull ParticleBuilder particleBuilder, double radius) {
        particleCircleWithDensity(particleBuilder, radius, 0.5);
    }

    public static void particleCircleWithDensity(@NotNull ParticleBuilder particleBuilder, double radius, double distanceBetweenParticles) {
        particleCircleWithAngle(particleBuilder, radius, 360d/(2d*Math.PI*radius/distanceBetweenParticles));
    }

    public static void particleCircleWithAngle(@NotNull ParticleBuilder particleBuilder, double radius, double angleDegrees) {
        Location location = particleBuilder.location();
        Preconditions.checkArgument(location != null, "Location must be not null");
        for (double d = 0; d <= 360; d += angleDegrees) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            particleLoc.setX(location.getX() + Math.cos(Math.toRadians(d)) * radius);
            particleLoc.setZ(location.getZ() + Math.sin(Math.toRadians(d)) * radius);
            particleBuilder.location(particleLoc);
            particleBuilder.spawn();
        }
    }
}
