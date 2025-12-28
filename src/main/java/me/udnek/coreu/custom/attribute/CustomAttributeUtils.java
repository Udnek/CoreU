package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.effect.CustomEffect;
import me.udnek.coreu.custom.enchantment.CustomEnchantment;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAttributeUtils {

    private final CustomAttribute attribute;
    private final Collection<CustomEquipmentSlot> searchTroughSlots;
    private final LivingEntity entity;
    private double amount;

    private double multiplyBase = 1;
    private double multiply = 1;
    private final boolean reversed;

    public CustomAttributeUtils(@NotNull CustomAttribute attribute, @NotNull Collection<CustomEquipmentSlot> searchTroughSlots, @NotNull LivingEntity entity, double base){
        this(attribute, searchTroughSlots, entity, base, false);
    }

    public CustomAttributeUtils(@NotNull CustomAttribute attribute, @NotNull Collection<CustomEquipmentSlot> searchTroughSlots, @NotNull LivingEntity entity, double base, boolean reversed){
        this.attribute = attribute;
        this.searchTroughSlots = searchTroughSlots;
        this.entity = entity;
        this.amount = base;
        this.reversed = reversed;
    }

    protected void calculate(){

        Map<CustomEquipmentSlot.Single, @NotNull ItemStack> slots = new HashMap<>();

        for (CustomEquipmentSlot slot : searchTroughSlots) {
            slot.getAllSingle(singleSlot ->
            {
                if (slots.containsKey(singleSlot)) return;
                UniversalInventorySlot universal = singleSlot.getUniversal();
                if (universal == null) return;
                ItemStack item = universal.getItem(entity);
                if (item == null) return;
                slots.put(singleSlot, item);
            });
        }

        for (Map.Entry<CustomEquipmentSlot.@NotNull Single, @NotNull ItemStack> slotEntry : slots.entrySet()) {
            ItemStack item = slotEntry.getValue();
            CustomEquipmentSlot.Single slot = slotEntry.getKey();
            for (Map.Entry<Enchantment, Integer> enchantmentEntry : item.getEnchantments().entrySet()) {
                CustomEnchantment enchantment = CustomEnchantment.get(enchantmentEntry.getKey());
                if (enchantment == null) continue;
                enchantment.getCustomAttributes(enchantmentEntry.getValue(), (localAttribute, modifier) -> {
                    if (localAttribute != attribute) return;
                    if (!modifier.getEquipmentSlot().intersects(entity, slot)) return;
                    add(modifier.getOperation(), modifier.getAmount());
                });
            }

            CustomItem customItem = CustomItem.get(item);
            if (customItem == null) continue;
            CustomAttributesContainer container = customItem.getComponents().getOrDefault(CustomComponentType.CUSTOM_ATTRIBUTED_ITEM).getAttributes();

            for (Map.Entry<CustomAttribute, List<CustomAttributeModifier>> entry : container.getAll().entrySet()) {
                if (entry.getKey() != attribute) continue;
                for (CustomAttributeModifier modifier : entry.getValue()) {
                    if (!modifier.getEquipmentSlot().intersects(entity, slot)) continue;
                    add(modifier.getOperation(), modifier.getAmount());
                }
            }

        }

        for (PotionEffect potionEffect : entity.getActivePotionEffects()) {
            CustomEffect custom = CustomEffect.get(potionEffect.getType());
            if (custom == null) continue;
            custom.getCustomAttributes(potionEffect, (thisAttr, modifier) -> {
                if (thisAttr != attribute) return;
                add(modifier.getOperation(), modifier.getAmount());
            });
        }


        amount *= multiplyBase;
        amount *= multiply;
    }

    public void add(@NotNull AttributeModifier.Operation operation, double localAmount){
        if (reversed) localAmount *= -1;
        switch (operation){
            case ADD_NUMBER -> amount += localAmount;
            case ADD_SCALAR -> multiplyBase += localAmount;
            case MULTIPLY_SCALAR_1 -> multiply *= (1 + localAmount);
        }
    }


    public static double calculate(@NotNull CustomAttribute attribute, @NotNull Collection<CustomEquipmentSlot> slots, @NotNull LivingEntity entity, double base, boolean reversed){
        CustomAttributeUtils attributeUtils = new CustomAttributeUtils(attribute, slots, entity, base, reversed);
        attributeUtils.calculate();
        return attributeUtils.amount;
    }

    public static double calculate(@NotNull CustomAttribute attribute, @NotNull LivingEntity entity, double base, boolean reversed){
        return calculate(attribute, CustomRegistries.EQUIPMENT_SLOT.getAll(), entity, base, reversed);
    }
}













