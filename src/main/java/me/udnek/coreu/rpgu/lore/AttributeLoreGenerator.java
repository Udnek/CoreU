package me.udnek.coreu.rpgu.lore;

import com.google.common.collect.Multimap;
import me.udnek.coreu.custom.attribute.*;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.util.ComponentU;
import me.udnek.coreu.util.LoreBuilder;
import me.udnek.coreu.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AttributeLoreGenerator {

    public static final TextColor MELEE_DESCRIPTION_COLOR = NamedTextColor.DARK_GREEN;
    public static final TextColor OTHER_DESCRIPTION_COLOR = NamedTextColor.BLUE;
    public static final TextColor HEADER_COLOR = NamedTextColor.GRAY;

    private static void generate(@NotNull ItemStack itemStack, @NotNull LoreBuilder builder){

        LoreBuilder.Componentable componentable = builder.get(LoreBuilder.Position.ATTRIBUTES);
        AttributesLorePart attributesLorePart;
        if (componentable instanceof AttributesLorePart){
            attributesLorePart = (AttributesLorePart) componentable;
        } else {
            attributesLorePart = new AttributesLorePart();
            builder.set(LoreBuilder.Position.ATTRIBUTES, attributesLorePart);
        }

        // VANILLA
        Multimap<Attribute, AttributeModifier> vanillaAttributes = AttributeUtils.getAttributes(itemStack);
        // CUSTOM
        CustomAttributesContainer customAttributes;
        // VANILLA-CUSTOM
        VanillaAttributesContainer vanillaCustomAttributes;

        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null){
            customAttributes = customItem.getComponents().getOrDefault(CustomComponentType.CUSTOM_ATTRIBUTED_ITEM).getAttributes();
            // VANILLA-CUSTOM
            vanillaCustomAttributes = customItem.getComponents().getOrDefault(CustomComponentType.VANILLA_ATTRIBUTED_ITEM).getAttributes();
        } else {
            customAttributes = CustomAttributesContainer.empty();
            vanillaCustomAttributes = VanillaAttributesContainer.empty();
        }


        for (CustomEquipmentSlot slot : CustomRegistries.EQUIPMENT_SLOT.getAll()) {

            // VANILLA
            EquipmentSlotGroup vanillaSlot = slot.getVanillaGroup();
            if (vanillaSlot != null){

                Multimap<Attribute, AttributeModifier> attributesBySlot = AttributeUtils.getAttributesBySlot(vanillaAttributes, vanillaSlot);
                Attribute[] sorted = sortAttributes(attributesBySlot);

                for (Attribute attribute : sorted) {
                    for (AttributeModifier modifier : attributesBySlot.get(attribute)) {
                        if (modifier.getAmount() == 0) continue;
                        attributesLorePart.addAttribute(slot, getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
                    }
                }
            }

            // CUSTOM
            CustomAttributesContainer customAttributesBySlot = customAttributes.getExact(slot);
            for (Map.Entry<CustomAttribute, List<CustomAttributeModifier>> entry : customAttributesBySlot.getAll().entrySet()) {
                CustomAttribute attribute = entry.getKey();
                for (CustomAttributeModifier modifier : entry.getValue()) {
                    if (modifier.getAmount() == 0) continue;
                    attributesLorePart.addAttribute(slot, getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
                }
            }

            // VANILLA-CUSTOM
            VanillaAttributesContainer vanillaCustomAttributesBySlot = vanillaCustomAttributes.getExact(slot);
            for (Map.Entry<Attribute, List<CustomKeyedAttributeModifier>> entry : vanillaCustomAttributesBySlot.getAll().entrySet()) {
                Attribute attribute = entry.getKey();
                for (CustomKeyedAttributeModifier modifier : entry.getValue()) {
                    if (modifier.getAmount() == 0) continue;
                    attributesLorePart.addAttribute(slot, getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
                }
            }


        }

        if (customItem != null){
            builder.add(LoreBuilder.Position.ID,
                    Component.text(customItem.getId()).color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
            );
        }
    }

    public static void generateVanillaAttributes(@NotNull ItemStack item, @NotNull LoreBuilder builder){
        LoreBuilder.Componentable componentable = builder.get(LoreBuilder.Position.ATTRIBUTES);
        AttributesLorePart attributesLorePart;
        if (componentable instanceof AttributesLorePart){
            attributesLorePart = (AttributesLorePart) componentable;
        } else {
            attributesLorePart = new AttributesLorePart();
            builder.set(LoreBuilder.Position.ATTRIBUTES, attributesLorePart);
        }

        Multimap<Attribute, AttributeModifier> attributes = AttributeUtils.getAttributes(item);

        for (Attribute attribute : sortAttributes(attributes)) {
            for (AttributeModifier modifier : attributes.get(attribute)) {
                if (modifier.getAmount() == 0) continue;
                CustomEquipmentSlot slot = CustomEquipmentSlot.getFromVanilla(modifier.getSlotGroup());
                attributesLorePart.addAttribute(slot, getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
            }
        }
    }


    public static @NotNull Attribute[] sortAttributes(@NotNull Multimap<Attribute, AttributeModifier> multimap){
        Attribute[] keys = new Attribute[multimap.keys().size()];
        multimap.keys().toArray(keys);
        Arrays.sort(keys, (a1, a2) -> {
            if      (a1 == Attribute.ATTACK_DAMAGE) return -1;
            else if (a2 == Attribute.ATTACK_DAMAGE) return 1;
            else if (a1 == Attribute.ATTACK_SPEED) return -1;
            else if (a2 == Attribute.ATTACK_SPEED) return 1;
            return 0;
        });

        return keys;
    }

    public static Component getAttributeLine(@NotNull CustomAttribute attribute, double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot slot){
        return attribute.getLoreLine(amount, operation);
    }

    public static Component getAttributeLine(@NotNull Attribute attribute, double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot slot){

        String key;
        TextColor color;
        if (amount < 0) {
            key = "attribute.modifier.take.";
            color = CustomAttribute.TAKE_COLOR;
        } else {
            key = "attribute.modifier.plus.";
            color = CustomAttribute.PLUS_COLOR;
        }

        if (attribute == Attribute.ATTACK_SPEED){
            amount += 4;
            key = "attribute.modifier.equals.";
        }
        else if (attribute == Attribute.ATTACK_DAMAGE){
            amount += 1;
            key = "attribute.modifier.equals.";
        }


        key += switch (operation){
            case ADD_NUMBER -> "0";
            case ADD_SCALAR -> "1";
            case MULTIPLY_SCALAR_1 -> "2";
        };

        if (operation != AttributeModifier.Operation.ADD_NUMBER) amount*=100d;

        return Component.translatable(key, Component.text(Utils.roundToTwoDigits(Math.abs(amount))), Component.translatable(attribute.translationKey())).color(color);
    }

    public static @NotNull Component getHeader(@NotNull CustomEquipmentSlot slot){
        String line = slot.translationKey();
        return Component.translatable(line).color(HEADER_COLOR).decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull Component addTab(@NotNull Component noOuter){
        return ComponentU.translatableWithInsertion("rpgu_equipment.coreu.description_tab", noOuter).decoration(TextDecoration.ITALIC, false);
    }

}
