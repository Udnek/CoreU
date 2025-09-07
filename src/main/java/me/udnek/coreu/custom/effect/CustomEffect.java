package me.udnek.coreu.custom.effect;

import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomEffect extends Registrable, ComponentHolder<CustomEffect>, Translatable {

    static boolean isCustom(@NotNull PotionEffectType bukkit){
        return CustomRegistries.EFFECT.contains(bukkit.key().asString());
    }
    static @Nullable CustomEffect get(@NotNull PotionEffectType bukkit){
        return CustomRegistries.EFFECT.get(bukkit.key().asString());
    }

    @NotNull PotionEffectType getBukkitType();

    @Override
    @NotNull
    default String translationKey(){
        return getBukkitType().translationKey();
    }

    void apply(@NotNull LivingEntity entity, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon);
    default void apply(@NotNull LivingEntity entity, @NotNull PotionEffect effect){
        apply(entity, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
    }
    default void apply(@NotNull LivingEntity entity, int duration, int amplifier){
        apply(entity, duration, amplifier, false, true, true);
    }
    default void applyInvisible(@NotNull LivingEntity entity, int duration, int amplifier){
        apply(entity, duration, amplifier, false, false, false);
    }
    default void remove(@NotNull LivingEntity entity){
        entity.removePotionEffect(getBukkitType());
    }

    boolean has(@NotNull LivingEntity entity);
    @Nullable PotionEffect get(@NotNull LivingEntity entity);
    default int getAppliedLevel(@NotNull LivingEntity entity){
        PotionEffect effect = get(entity);
        if (effect == null) return -1;
        return effect.getAmplifier();
    }
    void getCustomAttributes(@NotNull PotionEffect context, @NotNull CustomAttributeConsumer consumer);

}
