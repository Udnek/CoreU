package me.udnek.coreu.custom.attribute;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

@org.jspecify.annotations.NullMarked public class AttributeUtils{

    public static void removeAttribute(ItemStack itemStack, Attribute attribute){
        itemStack.editMeta(itemMeta -> itemMeta.removeAttributeModifier(attribute));
    }

    public static ItemAttributeModifiers addAttribute(@Nullable ItemAttributeModifiers data,
                                                      Attribute attribute,
                                                      AttributeModifier modifier,
                                                      EquipmentSlotGroup slot)
    {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes()
                // new
                .addModifier(attribute, modifier, slot);
        // already presented
        List<ItemAttributeModifiers.Entry> modifiers = data == null ? List.of() : data.modifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            builder.addModifier(entry.attribute(), entry.modifier(), entry.getGroup(), entry.display());
        }
        return builder.build();
    }

    public static ItemAttributeModifiers addAttribute(@Nullable ItemAttributeModifiers data,
                                                      Attribute attribute,
                                                      NamespacedKey id,
                                                      double amount,
                                                      AttributeModifier.Operation operation,
                                                      EquipmentSlotGroup slot)
    {
        return addAttribute(data, attribute, new AttributeModifier(id, amount, operation, slot), slot);
    }

    public static void addAttribute(ItemStack stack,
                                    Attribute attribute,
                                    NamespacedKey id,
                                    double amount,
                                    AttributeModifier.Operation operation,
                                    EquipmentSlotGroup slot)
    {
        ItemAttributeModifiers modifiers = addAttribute(stack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS),
                attribute, id, amount, operation, slot);
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributes(ItemStack itemStack){
        ItemAttributeModifiers data = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (data == null) return ArrayListMultimap.create(0, 0);
        return getAttributes(data);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributes(ItemAttributeModifiers data){
        ArrayListMultimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        data.modifiers().forEach(entry -> attributes.put(entry.attribute(), entry.modifier()));
        return attributes;
    }

    public static void appendAttribute(ItemStack stack,
                                       Attribute attribute,
                                       @UnknownNullability("can be null if item has attribute") NamespacedKey id,
                                       double amount,
                                       AttributeModifier.Operation operation,
                                       EquipmentSlotGroup slot)
    {
        ItemAttributeModifiers modifiers = appendAttribute(
                stack.getDataOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes().build()),
                attribute, id, amount, operation, slot);
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
    }

    public static ItemAttributeModifiers appendAttribute(ItemAttributeModifiers data,
                                                         Attribute attribute,
                                                         @UnknownNullability("can be null if item has attribute") NamespacedKey id,
                                                         double amount,
                                                         AttributeModifier.Operation operation,
                                                         EquipmentSlotGroup slot)
    {
        Multimap<Attribute, AttributeModifier> attributes = getAttributes(data);
        if (attributes.isEmpty()) {
            return addAttribute(data, attribute, id, amount, operation, slot);
        }

        ArrayListMultimap<Attribute, AttributeModifier> newAttributeMap = ArrayListMultimap.create();
        boolean wasAdded = false;
        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
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
        if (!wasAdded) {
            Preconditions.checkArgument(id != null, "Attribute doesn't present, specify id manually");
            newAttributeMap.put(attribute, new AttributeModifier(id, amount, operation, slot));
        }

        ItemAttributeModifiers.Builder newData = ItemAttributeModifiers.itemAttributes();
        for (Map.Entry<Attribute, AttributeModifier> entry : newAttributeMap.entries()) {
            Attribute attr = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            newData.addModifier(attr, modifier);
        }
        return newData.build();
    }
}
