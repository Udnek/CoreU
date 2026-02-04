package me.udnek.coreu.nms;

import me.udnek.coreu.util.Reflex;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class EnchantmentWrapper implements NmsWrapper<Enchantment>{

    protected Enchantment enchantment;

    public EnchantmentWrapper(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public void clearEffects(){
        setEffectsNms(null);
    }

    public void setEffectsNms(@Nullable DataComponentMap effects){
        if (effects == null) effects = DataComponentMap.EMPTY;
        Reflex.setRecordFieldValue(enchantment, "effects", effects);
    }

    public DataComponentMap getEffectsNms(){
        return Reflex.getFieldValue(enchantment, "effects");
    }

    public <T> void setEffect(DataComponentType<T> type, @Nullable T effect){
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.addAll(getEffectsNms());
        builder.set(type, effect);
        setEffectsNms(builder.build());
    }

    public void addEffect(NamespacedKey id, Attribute bukkitAttribute, float baseValue, float valueAboveFirst, org.bukkit.attribute.AttributeModifier.Operation bukkitOperation){
        AttributeModifier.Operation operation = switch (bukkitOperation){
            case ADD_NUMBER -> AttributeModifier.Operation.ADD_VALUE;
            case MULTIPLY_SCALAR_1 -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case ADD_SCALAR -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        };
        net.minecraft.world.entity.ai.attributes.Attribute attribute = NmsUtils.toNms(Registries.ATTRIBUTE, bukkitAttribute).value();
        EnchantmentAttributeEffect effect = new EnchantmentAttributeEffect(
                NmsUtils.toNmsIdentifier(id),
                NmsUtils.getRegistry(Registries.ATTRIBUTE).wrapAsHolder(attribute),
                LevelBasedValue.perLevel(baseValue, valueAboveFirst),
                operation
        );
        List<EnchantmentAttributeEffect> attributes = getEffectsNms().get(EnchantmentEffectComponents.ATTRIBUTES);
        if (attributes == null) attributes = new ArrayList<>();
        else attributes = new ArrayList<>(attributes);
        attributes.add(effect);
        setEffect(EnchantmentEffectComponents.ATTRIBUTES, attributes);
    }

    @Override
    public Enchantment getNms() {
        return enchantment;
    }
}
