package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

@org.jspecify.annotations.NullMarked public class CustomAttributeModifier{

    protected final double amount;
    protected final AttributeModifier.Operation operation;
    protected final CustomEquipmentSlot equipmentSlot;

    public CustomAttributeModifier(double amount, AttributeModifier.Operation operation){
        this(amount, operation, CustomEquipmentSlot.ANY_VANILLA);
    }

    public CustomAttributeModifier(double amount, AttributeModifier.Operation operation, CustomEquipmentSlot equipmentSlot){
        this.amount = amount;
        this.operation = operation;
        this.equipmentSlot = equipmentSlot;
    }
    public double getAmount() {return amount;}
    public AttributeModifier.Operation getOperation() {return operation;}
    public CustomEquipmentSlot getEquipmentSlot() {return equipmentSlot;}
    public AttributeModifier toVanilla(NamespacedKey key){
        EquipmentSlotGroup equipmentSlot = this.equipmentSlot.getVanillaGroup();
        if (equipmentSlot == null) equipmentSlot = EquipmentSlotGroup.ANY;
        return new AttributeModifier(key, amount, operation, equipmentSlot);
    }
}
