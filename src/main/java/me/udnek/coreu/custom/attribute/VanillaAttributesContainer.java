package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@NullMarked public class VanillaAttributesContainer extends AbstractAttributeContainer<Attribute, CustomKeyedAttributeModifier, VanillaAttributesContainer>{

    public static VanillaAttributesContainer empty(){return new VanillaAttributesContainer();}

    public static class Builder{

        private final VanillaAttributesContainer container;

        public Builder(){
            container = new VanillaAttributesContainer();
        }

        public Builder add(VanillaAttributesContainer container){
            for (Map.Entry<Attribute, List<CustomKeyedAttributeModifier>> entry : container.getAll().entrySet()) {
                for (@NonNull CustomKeyedAttributeModifier modifier : entry.getValue()) {
                    add(entry.getKey(), modifier);
                }
            }
            return this;
        }

        public Builder add(Attribute attribute, NamespacedKey key, double amount, AttributeModifier.Operation operation, CustomEquipmentSlot slot){
            CustomKeyedAttributeModifier attributeModifier = new CustomKeyedAttributeModifier(key, amount, operation, slot);
            return add(attribute, attributeModifier);
        }

        public Builder add(Attribute attribute, CustomKeyedAttributeModifier attributeModifier){
            container.add(attribute, attributeModifier);
            return this;
        }

        public VanillaAttributesContainer build(){
            return container;
        }
    }
}
