package me.udnek.coreu.custom.effect;

import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Constructor;

public abstract class ConstructableCustomEffect extends AbstractRegistrableComponentable<CustomEffect> implements CustomEffect{
    protected Holder<MobEffect> nmsEffect;
    protected PotionEffectType bukkitEffect;
    
    public abstract @NotNull PotionEffectTypeCategory getCategory();
    public int getColorIfDefaultParticle(){return Color.WHITE.getRGB();}
    public @Nullable Particle getParticle(){return null;}
    public @Nullable Sound getApplySound(){return null;}
    public void addAttributes(@NotNull AttributeConsumer consumer){}
    public void modifyParticleIfNotDefault(@NotNull ModifyParticleConsumer consumer){}
    @Override
    public void getCustomAttributes(@NotNull PotionEffect context, @NotNull CustomAttributeConsumer consumer) {}

    @Nullable
    public abstract PotionEffectType getVanillaDisguise();

    @Override
    public void initialize(@NotNull Plugin plugin) {
        super.initialize(plugin);
        MobEffectCategory category = switch (getCategory()){
            case HARMFUL -> MobEffectCategory.HARMFUL;
            case BENEFICIAL -> MobEffectCategory.BENEFICIAL;
            case NEUTRAL -> MobEffectCategory.NEUTRAL;
        };
        MobEffect mobEffect;
        if (getParticle() == null){
            Constructor<MobEffect> constructor = Reflex.getConstructor(MobEffect.class, MobEffectCategory.class, int.class);
            mobEffect = Reflex.construct(constructor, category, getColorIfDefaultParticle());
        } else {
            Constructor<MobEffect> constructor = Reflex.getConstructor(MobEffect.class, MobEffectCategory.class, int.class, ParticleOptions.class);
            ParticleType<?> particleType = NmsUtils.toNms(Registries.PARTICLE_TYPE, getParticle()).value();


            ModifyParticleConsumerWithReturn modifyParticleConsumer = new ModifyParticleConsumerWithReturn() {
                private ParticleOptions newParticle = null;
                @Override
                public @Nullable ParticleOptions getOptions() {return newParticle;}

                @Override
                public void color(int color) {
                    newParticle = ColorParticleOption.create((ParticleType<ColorParticleOption>) particleType, color);
                }
                @Override
                public void dustTransition(int fromColor, int toColor, float size) {
                    newParticle = new DustColorTransitionOptions(fromColor, toColor, size);
                }
            };
            modifyParticleIfNotDefault(modifyParticleConsumer);
            ParticleOptions particleOptions = modifyParticleConsumer.getOptions();

            if (particleOptions == null) {
                particleOptions = (ParticleOptions) particleType;
            }

            mobEffect = Reflex.construct(constructor, category, getColorIfDefaultParticle(), particleOptions);
        }

        if (getApplySound() != null) mobEffect.withSoundOnAdded(NmsUtils.toNms(Registries.SOUND_EVENT, getApplySound()).value());

        addAttributes((attribute, key, amount, operation) ->
                        mobEffect.addAttributeModifier(
                                NmsUtils.toNms(Registries.ATTRIBUTE, attribute),
                                NmsUtils.toNmsIdentifier(key),
                                amount,
                                NmsUtils.toNmsOperation(operation)
                        )
        );

        nmsEffect = NmsUtils.registerEffect(mobEffect, new NamespacedKey(plugin, getRawId()));

        Registry<MobEffect> registry = NmsUtils.getRegistry(Registries.MOB_EFFECT);
        bukkitEffect = PotionEffectType.getById(registry.getIdOrThrow(nmsEffect.value()) + 1);

        Reflex.setFieldValue(nmsEffect.value(), "descriptionId", translationKey());
    }

    @Override
    public void apply(@NotNull LivingEntity bukkit, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        net.minecraft.world.entity.LivingEntity entity = NmsUtils.toNmsEntity(bukkit);
        entity.addEffect(new MobEffectInstance(nmsEffect, duration, amplifier, ambient, showParticles, showIcon));
    }

    @Override
    public @NotNull PotionEffectType getBukkitType() {
        return bukkitEffect;
    }

    @Override
    public @Nullable PotionEffect get(@NotNull LivingEntity living) {
        return living.getPotionEffect(bukkitEffect);
    }

    @Override
    public boolean has(@NotNull LivingEntity entity) {
        return entity.hasPotionEffect(bukkitEffect);
    }

    public interface AttributeConsumer{
        void accept(@NotNull Attribute attribute, @NotNull Key key, double amount, @NotNull org.bukkit.attribute.AttributeModifier.Operation operation);
    }

    public interface ModifyParticleConsumer{
        void color(int color);
        void dustTransition(int fromColor, int toColor, float size);
    }

    private interface ModifyParticleConsumerWithReturn extends ModifyParticleConsumer{
        @Nullable ParticleOptions getOptions();
    }
}
