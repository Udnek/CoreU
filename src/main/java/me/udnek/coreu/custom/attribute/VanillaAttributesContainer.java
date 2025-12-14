package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class VanillaAttributesContainer extends AbstractAttributeContainer<Attribute, CustomKeyedAttributeModifier, VanillaAttributesContainer>{

    public static @NotNull VanillaAttributesContainer empty(){return new VanillaAttributesContainer();}

    @Override
    public @NotNull VanillaAttributesContainer get(@NotNull Predicate<@NotNull CustomEquipmentSlot> predicate) {
        VanillaAttributesContainer newContainer = new VanillaAttributesContainer();
        for (var entry : attributes.entrySet()) {
            Attribute attribute = entry.getKey();
            for (var modifier : entry.getValue()) {
                if (predicate.test(modifier.getEquipmentSlot())) newContainer.add(attribute, modifier);
            }

        }
        return newContainer;
    }


    public static class Builder{

        private final VanillaAttributesContainer container;
        
        public Builder(){
            container = new VanillaAttributesContainer();
        }

        public @NotNull VanillaAttributesContainer.Builder add(@NotNull VanillaAttributesContainer container){
            for (Map.Entry<@NotNull Attribute, @NotNull List<@NotNull CustomKeyedAttributeModifier>> entry : container.getAll().entrySet()) {
                for (@NotNull CustomKeyedAttributeModifier modifier : entry.getValue()) {
                    add(entry.getKey(), modifier);
                }
            }
            return this;
        }

        public @NotNull VanillaAttributesContainer.Builder add(@NotNull Attribute attribute, @NotNull NamespacedKey key, double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot slot){
            CustomKeyedAttributeModifier attributeModifier = new CustomKeyedAttributeModifier(key, amount, operation, slot);
            return add(attribute, attributeModifier);
        }

        public @NotNull VanillaAttributesContainer.Builder add(@NotNull Attribute attribute, @NotNull CustomKeyedAttributeModifier attributeModifier){
            container.add(attribute, attributeModifier);
            return this;
        }

        public @NotNull VanillaAttributesContainer build(){
            return container;
        }

    }
}
