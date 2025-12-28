package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

public class CustomAttributeModifier {

    protected final double amount;
    protected final AttributeModifier.Operation operation;
    protected final CustomEquipmentSlot equipmentSlot;

    public CustomAttributeModifier(double amount, @NotNull AttributeModifier.Operation operation){
        this(amount, operation, CustomEquipmentSlot.ANY_VANILLA);
    }

    public CustomAttributeModifier(double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot equipmentSlot){
        this.amount = amount;
        this.operation = operation;
        this.equipmentSlot = equipmentSlot;
    }
    public double getAmount() {return amount;}
    public @NotNull AttributeModifier.Operation getOperation() {return operation;}
    public @NotNull CustomEquipmentSlot getEquipmentSlot() {return equipmentSlot;}
    public @NotNull AttributeModifier toVanilla(@NotNull NamespacedKey key){
        EquipmentSlotGroup equipmentSlot = this.equipmentSlot.getVanillaGroup();
        if (equipmentSlot == null) equipmentSlot = EquipmentSlotGroup.ANY;
        return new AttributeModifier(key, amount, operation, equipmentSlot);
    }
}
