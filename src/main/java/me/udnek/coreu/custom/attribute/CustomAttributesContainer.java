package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@org.jspecify.annotations.NullMarked public class CustomAttributesContainer extends AbstractAttributeContainer<CustomAttribute, CustomAttributeModifier, CustomAttributesContainer>{

    private CustomAttributesContainer(){}

    public static CustomAttributesContainer empty(){return new CustomAttributesContainer();}

    public static class Builder{

        private final CustomAttributesContainer container;
        public Builder(){
            container = new CustomAttributesContainer();
        }
        public Builder add(CustomAttributesContainer container){
            for (Map.Entry<CustomAttribute, List<CustomAttributeModifier>> entry : container.getAll().entrySet()) {
                for (@NotNull CustomAttributeModifier modifier : entry.getValue()) {
                    add(entry.getKey(), modifier);
                }
            }
            return this;
        }
        public Builder add(CustomAttribute customAttribute, double amount, AttributeModifier.Operation operation, CustomEquipmentSlot slot){
            CustomAttributeModifier attributeModifier = new CustomAttributeModifier(amount, operation, slot);
            return add(customAttribute, attributeModifier);
        }

        public Builder add(CustomAttribute customAttribute, CustomAttributeModifier attributeModifier){
            container.add(customAttribute, attributeModifier);
            return this;
        }

        public CustomAttributesContainer build(){
            return container;
        }

    }

}
