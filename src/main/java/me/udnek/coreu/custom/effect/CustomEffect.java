package me.udnek.coreu.custom.effect;

import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  interface CustomEffect extends Registrable, ComponentHolder<CustomEffect>, Translatable{

    static boolean isCustom(PotionEffectType bukkit){
        return CustomRegistries.EFFECT.contains(bukkit.key().asString());
    }
    static @Nullable CustomEffect get(PotionEffectType bukkit){
        return CustomRegistries.EFFECT.get(bukkit.key().asString());
    }

    PotionEffectType getBukkitType();

    @Override
    default String translationKey(){
        return getBukkitType().translationKey();
    }

    void apply(LivingEntity entity, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon);

    default void apply(LivingEntity entity, PotionEffect effect){
        apply(entity, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
    }
    default void apply(LivingEntity entity, int duration, int amplifier){
        apply(entity, duration, amplifier, false, true, true);
    }
    default void applyInvisible(LivingEntity entity, int duration, int amplifier){
        apply(entity, duration, amplifier, false, false, false);
    }
    default void remove(LivingEntity entity){
        entity.removePotionEffect(getBukkitType());
    }

    default boolean has(LivingEntity entity){
        return get(entity) != null;
    }
    @Nullable PotionEffect get(LivingEntity entity);
    default int getAppliedLevel(LivingEntity entity){
        PotionEffect effect = get(entity);
        if (effect == null) return -1;
        return effect.getAmplifier();
    }
    void getCustomAttributes(PotionEffect context, CustomAttributeConsumer consumer);

}
