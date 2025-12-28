package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomAttributesContainer extends AbstractAttributeContainer<CustomAttribute, CustomAttributeModifier, CustomAttributesContainer>{

    private CustomAttributesContainer(){}

    public static @NotNull CustomAttributesContainer empty(){return new CustomAttributesContainer();}

//    @Override
//    public @NotNull CustomAttributesContainer get(@NotNull Predicate<@NotNull CustomEquipmentSlot> predicate) {
//        CustomAttributesContainer newContainer = new CustomAttributesContainer();
//        for (Map.Entry<CustomAttribute, List<CustomAttributeModifier>> entry : attributes.entrySet()) {
//            CustomAttribute attribute = entry.getKey();
//            for (CustomAttributeModifier modifier : entry.getValue()) {
//                if (predicate.test(modifier.getEquipmentSlot())) newContainer.add(attribute, modifier);
//            }
//        }
//        return newContainer;
//    }

    public static class Builder{

        private final CustomAttributesContainer container;
        public Builder(){
            container = new CustomAttributesContainer();
        }
        public @NotNull Builder add(@NotNull CustomAttributesContainer container){
            for (Map.Entry<@NotNull CustomAttribute, @NotNull List<@NotNull CustomAttributeModifier>> entry : container.getAll().entrySet()) {
                for (@NotNull CustomAttributeModifier modifier : entry.getValue()) {
                    add(entry.getKey(), modifier);
                }
            }
            return this;
        }
        public @NotNull Builder add(@NotNull CustomAttribute customAttribute, double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot slot){
            CustomAttributeModifier attributeModifier = new CustomAttributeModifier(amount, operation, slot);
            return add(customAttribute, attributeModifier);
        }

        public @NotNull Builder add(@NotNull CustomAttribute customAttribute, @NotNull CustomAttributeModifier attributeModifier){
            container.add(customAttribute, attributeModifier);
            return this;
        }

        public @NotNull CustomAttributesContainer build(){
            return container;
        }

    }

}
