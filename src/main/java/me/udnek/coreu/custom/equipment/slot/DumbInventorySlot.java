package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class DumbInventorySlot extends AbstractCustomEquipmentSlot implements CustomEquipmentSlot.Group{
    public DumbInventorySlot(String id) {
        super(id);
    }

    @Override
    public boolean intersects(LivingEntity entity, CustomEquipmentSlot slot) {
        return true;
    }

    @Override
    public boolean intersects(LivingEntity entity, UniversalInventorySlot slot) {
        return true;
    }

    @Override
    public @Nullable EquipmentSlotGroup getVanillaGroup() {return null;}
    @Override
    public @Nullable EquipmentSlot getVanillaSlot() {return null;}
    @Override
    public void getAllUniversal(Consumer<UniversalInventorySlot> consumer) {}
    @Override
    public void getAllSingle(Consumer<Single> consumer) {}

    @Override
    public String translationKey() {
        return "slot.coreu.dumb_inventory";
    }
}
