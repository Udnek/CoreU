package me.udnek.coreu.custom.effect;

import com.google.common.base.Preconditions;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.attribute.CustomAttributeModifier;
import me.udnek.coreu.custom.component.AbstractComponentHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
public class VanillaBasedEffect extends AbstractComponentHolder<CustomEffect>implements CustomEffect{

    private @UnknownNullability String id;
    private final PotionEffectType vanilla;
    private final List<Function<PotionEffect, Pair<CustomAttribute, CustomAttributeModifier>>> attributes = new ArrayList<>(0);

    VanillaBasedEffect(PotionEffectType vanilla){
        this.vanilla = vanilla;
    }

    @Override
    public void initialize(Plugin plugin) {
        Preconditions.checkArgument(id == null, "Registrable already initialized!");
        id = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).getKey(vanilla).asString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PotionEffectType getBukkitType() {
        return vanilla;
    }

    @Override
    public void apply(LivingEntity entity, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        entity.addPotionEffect(new PotionEffect(vanilla, duration, amplifier, ambient, showParticles, showIcon));
    }

    @Override
    public @Nullable PotionEffect get(LivingEntity entity) {
        return entity.getPotionEffect(vanilla);
    }

    public void addCustomAttribute(Function<PotionEffect, Pair<CustomAttribute, CustomAttributeModifier>> function){
        attributes.add(function);
    }

    @Override
    public void getCustomAttributes(PotionEffect context, CustomAttributeConsumer consumer) {
        for (Function<PotionEffect, Pair<CustomAttribute, CustomAttributeModifier>> function : attributes) {
            Pair<CustomAttribute, CustomAttributeModifier> pair = function.apply(context);
            consumer.accept(pair.getLeft(), pair.getRight());
        }
    }
}
