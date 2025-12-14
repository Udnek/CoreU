package me.udnek.coreu.custom.attribute;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Map;

public class AttributeUtils {
    public static void addDefaultAttributes(@NotNull ItemStack itemStack){
        itemStack.editMeta(itemMeta -> addDefaultAttributes(itemMeta, itemStack.getType()));
    }
    private static void addDefaultAttributes(@NotNull ItemMeta itemMeta, @NotNull Material material){
        Multimap<Attribute, AttributeModifier> attributeModifiers = material.getDefaultAttributeModifiers();
        for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers.entries()) {
            // todo remove when fixed
            AttributeModifier oldModifier = entry.getValue();
            NamespacedKey namespacedKey = new NamespacedKey(oldModifier.getKey().getNamespace(), oldModifier.getKey().getKey() + "_crutch");
            itemMeta.addAttributeModifier(entry.getKey(), new AttributeModifier(namespacedKey, oldModifier.getAmount(), oldModifier.getOperation(), oldModifier.getSlotGroup()));
        }
    }

    public static void removeAttribute(@NotNull ItemStack itemStack, @NotNull Attribute attribute){
        itemStack.editMeta(itemMeta -> itemMeta.removeAttributeModifier(attribute));
    }
    public static void removeAttribute(@NotNull ItemStack itemStack, @NotNull EquipmentSlot slot){
        itemStack.editMeta(itemMeta -> itemMeta.removeAttributeModifier(slot));
    }


    private static void addAttribute(@NotNull ItemMeta itemMeta, @NotNull Attribute attribute, @NotNull NamespacedKey id, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slot){
        itemMeta.addAttributeModifier(attribute, new AttributeModifier(id, amount, operation, slot));
    }
    public static void addAttribute(@NotNull ItemStack itemStack, @NotNull Attribute attribute, @NotNull NamespacedKey id, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slot){
        itemStack.editMeta(itemMeta -> addAttribute(itemMeta, attribute, id, amount, operation, slot));
    }

    public static @NotNull Multimap<Attribute, AttributeModifier> getAttributes(@NotNull ItemStack itemStack){
        ArrayListMultimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        ItemAttributeModifiers data = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (data == null) return attributes;
        data.modifiers().forEach(entry -> attributes.put(entry.attribute(), entry.modifier()));
        return attributes;
    }

    public static void appendAttribute(@NotNull ItemStack itemStack, @NotNull Attribute attribute, @UnknownNullability("can be null if item has attribute") NamespacedKey id, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slot){
        itemStack.editMeta(itemMeta -> appendAttribute(itemMeta, attribute, id, amount, operation, slot));
    }

    private static void appendAttribute(@NotNull ItemMeta itemMeta, @NotNull Attribute attribute, @UnknownNullability("can be null if item has attribute") NamespacedKey id,  double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slot) {
        if (!itemMeta.hasAttributeModifiers()) {
            addAttribute(itemMeta, attribute, id, amount, operation, slot);
            return;
        }
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(attribute);
        if (attributeModifiers == null || attributeModifiers.isEmpty()){
            addAttribute(itemMeta, attribute, id, amount, operation, slot);
            return;
        }

        ArrayListMultimap<Attribute, AttributeModifier> newAttributeMap = ArrayListMultimap.create();
        boolean wasAdded = false;
        for (Map.Entry<Attribute, AttributeModifier> entry : itemMeta.getAttributeModifiers().entries()) {
            Attribute thisAttribute = entry.getKey();
            AttributeModifier thisModifier = entry.getValue();

            if (
                !wasAdded &&
                thisAttribute == attribute &&
                thisModifier.getSlotGroup() == slot &&
                thisModifier.getOperation() == operation
            )
            {
                AttributeModifier newAttributeModifier = new AttributeModifier(
                        thisModifier.getKey(),
                        amount + thisModifier.getAmount(),
                        operation,
                        slot);
                newAttributeMap.put(thisAttribute, newAttributeModifier);
                wasAdded = true;
            }
            else newAttributeMap.put(thisAttribute, thisModifier);
        }
        if (!wasAdded) newAttributeMap.put(
                attribute,
                new AttributeModifier(id, amount, operation, slot));
        itemMeta.setAttributeModifiers(newAttributeMap);
    }

    public static @NotNull Multimap<Attribute, AttributeModifier> getAttributesBySlot(@NotNull Multimap<Attribute, AttributeModifier> attributes, @NotNull EquipmentSlotGroup slot){
        ArrayListMultimap<Attribute, AttributeModifier> newAttributes = ArrayListMultimap.create();
        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            AttributeModifier modifier = entry.getValue();
            if (modifier.getSlotGroup() != slot) continue;
            newAttributes.put(entry.getKey(), modifier);
        }
        return newAttributes;
    }

}
