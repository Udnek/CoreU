package me.udnek.coreu.rpgu.component.ability.property;


import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.effect.CustomEffect;
import me.udnek.coreu.rpgu.attribute.RPGUAttributes;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.component.ability.property.function.Modifiers;
import me.udnek.coreu.rpgu.component.ability.property.function.PropertyFunctions;
import me.udnek.coreu.rpgu.component.ability.property.function.RPGUPropertyFunction;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@org.jspecify.annotations.NullMarked public class EffectsProperty implements RPGUAbilityProperty<LivingEntity, List<PotionEffect>>{

    public static EffectsProperty DEFAULT = new EffectsProperty(){
        @Override
        public void add(PotionData data) {throwCanNotChangeDefault();}
    };

    protected List<PotionData> effects = new ArrayList<>();

    public EffectsProperty(){}

    public EffectsProperty(PotionData ...data){
        Arrays.stream(data).forEach(this::add);
    }

    public void add(PotionData data){effects.add(data);}

    @Override
    public List<PotionEffect> getBase() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (PotionData data : effects) {
            RPGUPropertyFunction<LivingEntity, Integer> duration = data.duration;
            potionEffects.add(new PotionEffect(data.type, duration.getBase(), data.amplifier.getBase(), data.ambient, data.particles, data.icon));
        }
        return potionEffects;
    }

    @Override
    public List<PotionEffect> get(LivingEntity livingEntity) {
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (PotionData data : effects) {
            RPGUPropertyFunction<LivingEntity, Integer> duration = data.duration;
            potionEffects.add(new PotionEffect(data.type, duration.apply(livingEntity), data.amplifier.apply(livingEntity), data.ambient, data.particles, data.icon));
        }
        return potionEffects;
    }

    public Component describeSingle(int index){
        PotionData data = effects.get(index);

        List<Component> args = new ArrayList<>();
        args.add(Component.translatable(data.type.translationKey()));

        String translation = "rpgu_ability_property.coreu.effects.";
        if (!data.amplifier.isZeroConstant()) {
            translation += "with_amplifier";
            args.add(data.amplifier.isConstant() ? Component.translatable("potion.potency." + data.amplifier.getBase()) : data.amplifier.describe().join());
        }
        else translation += "no_amplifier";

        if (!data.duration.isZeroConstant()) {
            translation += "_with_duration";
            args.add(data.duration.describeWithModifier(Modifiers.TICKS_TO_SECONDS()).join());
        }
        else translation += "_no_duration";

        return Component.translatable(translation, args);
    }

    @Override
    public void describe(AbilityLorePart componentable) {
        if (effects.size() > 1){
            componentable.addAbilityProperty(Component.translatable("rpgu_ability_property.coreu.effects", Component.empty()));
            for (int i = 0; i < effects.size(); i++) {
                componentable.addAbilityPropertyDoubleTab(describeSingle(i));
            }
        } else {
            Component text = Component.empty();
            for (int i = 0; i < effects.size(); i++) {
                text = text.append(describeSingle(i));
                if (i != effects.size()-1) text = text.append(Component.text(", "));
            }
            componentable.addAbilityProperty(Component.translatable("rpgu_ability_property.coreu.effects", List.of(text)));
        }
    }

    public void applyOn(LivingEntity source, LivingEntity target){
        for (PotionEffect effect : get(source)) {
            CustomEffect customEffect = CustomEffect.get(effect.getType());
            if (customEffect == null) target.addPotionEffect(effect);
            else customEffect.apply(target, effect);
        }
    }

    public void applyOn(LivingEntity source, Iterable<LivingEntity> targets){
        for (PotionEffect effect : get(source)) {
            CustomEffect customEffect = CustomEffect.get(effect.getType());
            for (LivingEntity target : targets) {
                if (customEffect == null) target.addPotionEffect(effect);
                else customEffect.apply(target, effect);
            }
        }
    }

    @Override
    public CustomComponentType<RPGUItemAbility<?>, ?> getType() {
        return RPGUComponents.ABILITY_EFFECTS;
    }

    public record PotionData(
            PotionEffectType type,
            RPGUPropertyFunction<LivingEntity, Integer> duration,
            RPGUPropertyFunction<LivingEntity, Integer> amplifier,
            boolean ambient,
            boolean particles,
            boolean icon
    ){

        public static RPGUPropertyFunction<LivingEntity, Integer> functionFromDuration(int duration){
            return PropertyFunctions.CEIL(PropertyFunctions.ATTRIBUTE_WITH_BASE(RPGUAttributes.ABILITY_DURATION, duration));
        }

        public PotionData(PotionEffectType type, RPGUPropertyFunction<LivingEntity, Integer> duration, RPGUPropertyFunction<LivingEntity, Integer> amplifier){
            this(type, duration, amplifier, false, true, true);
        }

        public PotionData(PotionEffect effect){
            this(
                    effect.getType(),
                    functionFromDuration(effect.getDuration()),
                    PropertyFunctions.CONSTANT(effect.getAmplifier()),
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            );
        }

        public PotionData(PotionEffectType type, int duration, int amplifier){
            this(type, functionFromDuration(duration), PropertyFunctions.CONSTANT(amplifier));
        }
    }
}




















