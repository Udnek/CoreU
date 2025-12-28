package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DumbInventorySlot extends AbstractCustomEquipmentSlot implements CustomEquipmentSlot.Group {
    public DumbInventorySlot(@NotNull String id) {
        super(id);
    }

    @Override
    public boolean intersects(@NotNull LivingEntity entity, @NotNull CustomEquipmentSlot slot) {
        return true;
    }

    @Override
    public boolean intersects(@NotNull LivingEntity entity, @NotNull UniversalInventorySlot slot) {
        return true;
    }

    @Override
    public @Nullable EquipmentSlotGroup getVanillaGroup() {return null;}
    @Override
    public @Nullable EquipmentSlot getVanillaSlot() {return null;}
    @Override
    public void getAllUniversal(@NotNull Consumer<@NotNull UniversalInventorySlot> consumer) {}
    @Override
    public void getAllSingle(@NotNull Consumer<@NotNull Single> consumer) {}

    @Override
    public @NotNull String translationKey() {
        return "slot.coreu.dumb_inventory";
    }
}
