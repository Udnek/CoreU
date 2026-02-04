package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;

@org.jspecify.annotations.NullMarked public class CustomKeyedAttributeModifier extends CustomAttributeModifier implements Keyed{
    protected final NamespacedKey key;
    public CustomKeyedAttributeModifier(NamespacedKey key, double amount, AttributeModifier.Operation operation, CustomEquipmentSlot equipmentSlot) {
        super(amount, operation, equipmentSlot);
        this.key = key;
    }

    public AttributeModifier toVanilla(){
        return toVanilla(key);
    }

    public AttributeModifier toVanillaWitAdjustedKey(String string){
        return toVanilla(new NamespacedKey(key.getNamespace(), key.getKey() + string));
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
