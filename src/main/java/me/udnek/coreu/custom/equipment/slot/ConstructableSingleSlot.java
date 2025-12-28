package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConstructableSingleSlot extends AbstractCustomEquipmentSlot implements CustomEquipmentSlot.Single {

    protected final String translation;
    protected final EquipmentSlotGroup vanillaGroup;
    protected final EquipmentSlot vanillaSlot;
    protected final UniversalInventorySlot universalSlot;

    public ConstructableSingleSlot(@NotNull String rawId, @Nullable EquipmentSlotGroup vanillaGroup, @Nullable EquipmentSlot vanillaSlot, @Nullable UniversalInventorySlot universalSlot, @NotNull String translation){
        super(rawId);
        this.translation = translation;
        this.vanillaGroup = vanillaGroup;
        this.vanillaSlot = vanillaSlot;
        this.universalSlot = universalSlot;
    }

    @Override
    public @Nullable UniversalInventorySlot getUniversal() {return universalSlot;}

    @Override
    public @Nullable EquipmentSlotGroup getVanillaGroup() {
        return vanillaGroup;
    }
    @Override
    public @Nullable EquipmentSlot getVanillaSlot() {return vanillaSlot;}

    @Override
    public @NotNull String translationKey() {return translation;}
}
