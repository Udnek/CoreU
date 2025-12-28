package me.udnek.coreu.custom.enchantment;

import me.udnek.coreu.nms.NmsContainer;
import me.udnek.coreu.nms.NmsUtils;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NmsEnchantmentContainer extends NmsContainer<Enchantment> {
    public NmsEnchantmentContainer(@NotNull Enchantment supply) {
        super(supply);
    }

    public void clearEffects(){
        setEffects(null);
    }

    public void setEffects(@Nullable DataComponentMap effects){
        if (effects == null) effects = DataComponentMap.EMPTY;
        Reflex.setRecordFieldValue(supply, "effects", effects);
    }

    public DataComponentMap getEffects(){
        return (DataComponentMap) Reflex.getFieldValue(supply, "effects");
    }

    public <T> void setEffect(@NotNull DataComponentType<T> type, @Nullable T effect){
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.addAll(getEffects());
        builder.set(type, effect);
        setEffects(builder.build());
    }

    public void addEffect(@NotNull NamespacedKey id, @NotNull Attribute bukkitAttribute, float baseValue, float valueAboveFirst, @NotNull org.bukkit.attribute.AttributeModifier.Operation bukkitOperation){
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
        List<EnchantmentAttributeEffect> attributes = getEffects().get(EnchantmentEffectComponents.ATTRIBUTES);
        if (attributes == null) attributes = new ArrayList<>();
        else attributes = new ArrayList<>(attributes);
        attributes.add(effect);
        setEffect(EnchantmentEffectComponents.ATTRIBUTES, attributes);
    }
}
