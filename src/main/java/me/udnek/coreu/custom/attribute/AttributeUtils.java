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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

public class AttributeUtils {

    public static void removeAttribute(@NotNull ItemStack itemStack, @NotNull Attribute attribute){
        itemStack.editMeta(itemMeta -> itemMeta.removeAttributeModifier(attribute));
    }

    public static @NotNull ItemAttributeModifiers addAttribute(@Nullable ItemAttributeModifiers data,
                                                               @NotNull Attribute attribute,
                                                               @NotNull AttributeModifier modifier,
                                                               @NotNull EquipmentSlotGroup slot)
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

    public static @NotNull ItemAttributeModifiers addAttribute(@Nullable ItemAttributeModifiers data,
                                                               @NotNull Attribute attribute,
                                                               @NotNull NamespacedKey id,
                                                               double amount,
                                                               @NotNull AttributeModifier.Operation operation,
                                                               @NotNull EquipmentSlotGroup slot)
    {
        return addAttribute(data, attribute, new AttributeModifier(id, amount, operation, slot), slot);
    }

    public static void addAttribute(@NotNull ItemStack stack,
                                    @NotNull Attribute attribute,
                                    @NotNull NamespacedKey id,
                                    double amount,
                                    @NotNull AttributeModifier.Operation operation,
                                    @NotNull EquipmentSlotGroup slot)
    {
        ItemAttributeModifiers modifiers = addAttribute(stack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS),
                attribute, id, amount, operation, slot);
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
    }

    public static @NotNull Multimap<@NotNull Attribute, @NotNull AttributeModifier> getAttributes(@NotNull ItemStack itemStack){
        ItemAttributeModifiers data = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (data == null) return ArrayListMultimap.create(0, 0);
        return getAttributes(data);
    }

    public static @NotNull Multimap<@NotNull Attribute, @NotNull AttributeModifier> getAttributes(@NotNull ItemAttributeModifiers data){
        ArrayListMultimap<@NotNull Attribute, @NotNull AttributeModifier> attributes = ArrayListMultimap.create();
        data.modifiers().forEach(entry -> attributes.put(entry.attribute(), entry.modifier()));
        return attributes;
    }

    public static void appendAttribute(@NotNull ItemStack stack,
                                      @NotNull Attribute attribute,
                                      @UnknownNullability("can be null if item has attribute") NamespacedKey id,
                                      double amount,
                                      @NotNull AttributeModifier.Operation operation,
                                      @NotNull EquipmentSlotGroup slot)
    {
        ItemAttributeModifiers modifiers = appendAttribute(
                stack.getDataOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes().build()),
                attribute, id, amount, operation, slot);
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
    }

    public static @NotNull ItemAttributeModifiers appendAttribute(@NotNull ItemAttributeModifiers data,
                                        @NotNull Attribute attribute,
                                        @UnknownNullability("can be null if item has attribute") NamespacedKey id,
                                        double amount,
                                        @NotNull AttributeModifier.Operation operation,
                                        @NotNull EquipmentSlotGroup slot)
    {
        Multimap<@NotNull Attribute, @NotNull AttributeModifier> attributes = getAttributes(data);
        if (attributes.isEmpty()) {
            return addAttribute(data, attribute, id, amount, operation, slot);
        }

        ArrayListMultimap<@NotNull Attribute, @NotNull AttributeModifier> newAttributeMap = ArrayListMultimap.create();
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
        for (Map.Entry<@NotNull Attribute, @NotNull AttributeModifier> entry : newAttributeMap.entries()) {
            Attribute attr = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            newData.addModifier(attr, modifier);
        }
        return newData.build();
    }
}
