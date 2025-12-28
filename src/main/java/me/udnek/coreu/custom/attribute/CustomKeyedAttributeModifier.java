package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class CustomKeyedAttributeModifier extends CustomAttributeModifier implements Keyed {
    protected final NamespacedKey key;
    public CustomKeyedAttributeModifier(@NotNull NamespacedKey key, double amount, @NotNull AttributeModifier.Operation operation, @NotNull CustomEquipmentSlot equipmentSlot) {
        super(amount, operation, equipmentSlot);
        this.key = key;
    }

    public @NotNull AttributeModifier toVanilla(){
        return toVanilla(key);
    }

    public @NotNull AttributeModifier toVanillaWitAdjustedKey(@NotNull String string){
        return toVanilla(new NamespacedKey(key.getNamespace(), key.getKey() + string));
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
